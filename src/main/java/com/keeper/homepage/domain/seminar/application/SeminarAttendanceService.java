package com.keeper.homepage.domain.seminar.application;

import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.ABSENCE;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.ATTENDANCE;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.BEFORE_ATTENDANCE;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.LATENESS;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.getSeminarAttendanceStatusBy;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_ATTEMPT_NOT_AVAILABLE;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_CODE_NOT_AVAILABLE;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_DUPLICATE;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_UNABLE;

import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.merit.application.MeritLogService;
import com.keeper.homepage.domain.seminar.application.convenience.ValidSeminarFindService;
import com.keeper.homepage.domain.seminar.dao.SeminarAttendanceRepository;
import com.keeper.homepage.domain.seminar.dto.request.SeminarAttendanceStatusRequest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarAttendanceResponse;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.redis.RedisUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeminarAttendanceService {

  private final RedisUtil redisUtil;
  private final ValidSeminarFindService validSeminarFindService;
  private final MemberFindService memberFindService;
  private final SeminarAttendanceRepository attendanceRepository;
  private final MeritLogService meritLogService;

  private static final int MAX_ATTEMPT_COUNT = 5;
  private static final int ONE_HOUR = 60 * 60;

  @Transactional
  public SeminarAttendanceResponse attendance(Long seminarId, Member member, String attendanceCode) {
    Seminar seminar = validSeminarFindService.findById(seminarId);
    SeminarAttendance seminarAttendance = attendanceRepository.findBySeminarAndMember(seminar, member)
        .orElseThrow(() -> new BusinessException(member.getRealName(), "member", SEMINAR_ATTENDANCE_UNABLE));

    String key = "seminar:" + seminar.getId() + ":memberId:" + member.getId();
    checkAttemptNumberLimit(key);
    checkAttendanceCode(seminar, attendanceCode, key);
    checkDuplicateAttendance(seminarAttendance);

    SeminarAttendanceStatusType type = seminar.getStatus().getType();
    seminarAttendance.changeStatus(type);
    giveAttendanceDemerit(type, member);
    return SeminarAttendanceResponse.of(seminarAttendance);
  }

  private void checkAttemptNumberLimit(String key) {
    int attemptNumber = redisUtil.increaseAndGetWithExpire(key, ONE_HOUR).intValue();
    if (attemptNumber > MAX_ATTEMPT_COUNT) {
      throw new BusinessException(attemptNumber, "attemptNumber", SEMINAR_ATTENDANCE_ATTEMPT_NOT_AVAILABLE);
    }
  }

  private void checkAttendanceCode(Seminar seminar, String attendanceCode, String key) {
    String seminarAttendanceCode = seminar.getAttendanceCode();

    if (!seminarAttendanceCode.equals(attendanceCode)) {
      String attemptNumber = redisUtil.getData(key, String.class).orElseThrow();
      throw new BusinessException(attemptNumber, "attemptNumber", SEMINAR_ATTENDANCE_CODE_NOT_AVAILABLE);
    }
  }

  private void checkDuplicateAttendance(SeminarAttendance seminarAttendance) {
    if (seminarAttendance.getSeminarAttendanceStatus().getType() != BEFORE_ATTENDANCE) {
      throw new BusinessException(seminarAttendance.getSeminarAttendanceStatus(), "attendanceStatus",
          SEMINAR_ATTENDANCE_DUPLICATE);
    }
  }

  private void giveAttendanceDemerit(SeminarAttendanceStatusType type, Member member) {
    if (type == ATTENDANCE) {
      return;
    }
    if (type == LATENESS) {
      if (member.isDualLateness()) {
        meritLogService.giveDualSeminarLatenessDemerit(member);
      }
      return;
    }
    if (type == ABSENCE) {
      meritLogService.giveSeminarAbsenceDemerit(member);
    }
  }

  @Transactional
  public void changeStatus(long seminarId, long memberId, SeminarAttendanceStatusRequest request) {
    Member member = memberFindService.findById(memberId);
    Seminar seminar = validSeminarFindService.findById(seminarId);

    SeminarAttendance seminarAttendance = attendanceRepository.findBySeminarAndMember(seminar, member)
        .orElseThrow(() -> new BusinessException(member.getRealName(), "realName", MEMBER_NOT_FOUND));

    seminarAttendance.changeStatus(request.excuse(), request.statusType());
  }

  @Scheduled(cron = "0 0 0 ? * 5", zone = "Asia/Seoul") // 매주 금요일 자정에 실행
  public void changeAllBeforeAttendanceToAbsence() {
    List<SeminarAttendance> beforeAttendances = attendanceRepository
        .findAllBySeminarAttendanceStatus(getSeminarAttendanceStatusBy(BEFORE_ATTENDANCE));

    beforeAttendances.forEach(seminarAttendance -> {
      seminarAttendance.changeStatus((ABSENCE));
      meritLogService.giveSeminarAbsenceDemerit(seminarAttendance.getMember());
    });
  }
}
