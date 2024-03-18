package com.keeper.homepage.domain.seminar.application;

import static com.keeper.homepage.domain.member.entity.type.MemberType.MemberTypeEnum.정회원;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.BEFORE_ATTENDANCE;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.LATENESS;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.PERSONAL;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.getSeminarAttendanceStatusBy;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_ATTEMPT_NOT_AVAILABLE;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_CODE_NOT_AVAILABLE;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_DUPLICATE;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_UNABLE;

import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.seminar.application.convenience.ValidSeminarFindService;
import com.keeper.homepage.domain.seminar.dao.SeminarAttendanceRepository;
import com.keeper.homepage.domain.seminar.dto.response.SeminarAttendanceManageResponse;
import com.keeper.homepage.domain.seminar.dto.response.SeminarAttendanceResponse;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.redis.RedisUtil;
import com.keeper.homepage.global.util.semester.SemesterUtil;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  private static final int MAX_ATTEMPT_COUNT = 5;
  private static final int ONE_HOUR = 60 * 60;

  @Transactional
  public SeminarAttendanceResponse attendance(Long seminarId, Member member,
      String attendanceCode) {
    Seminar seminar = validSeminarFindService.findById(seminarId);

    SeminarAttendance seminarAttendance = getSeminarAttendance(seminar, member);

    String key = "seminar:" + seminar.getId() + ":memberId:" + member.getId();
    checkAttemptNumberLimit(key);
    checkAttendanceCode(seminar, attendanceCode, key);
    checkDuplicateAttendance(seminarAttendance);

    SeminarAttendanceStatusType type = seminar.getStatus().getType();
    seminarAttendance.changeStatus(type);
    return SeminarAttendanceResponse.of(seminarAttendance);
  }

  private SeminarAttendance getSeminarAttendance(Seminar seminar, Member member) {
    if (!attendanceRepository.existsBySeminarAndMember(seminar, member) && member.isType(정회원)) {
      return attendanceRepository.save(SeminarAttendance.builder()
          .seminar(seminar)
          .member(member)
          .seminarAttendanceStatus(getSeminarAttendanceStatusBy(BEFORE_ATTENDANCE))
          .build());
    }
    return attendanceRepository.findBySeminarAndMember(seminar, member)
        .orElseThrow(
            () -> new BusinessException(member.getRealName(), "member", SEMINAR_ATTENDANCE_UNABLE));
  }

  private void checkAttemptNumberLimit(String key) {
    int attemptNumber = redisUtil.increaseAndGetWithExpire(key, ONE_HOUR).intValue();
    if (attemptNumber > MAX_ATTEMPT_COUNT) {
      throw new BusinessException(attemptNumber, "attemptNumber",
          SEMINAR_ATTENDANCE_ATTEMPT_NOT_AVAILABLE);
    }
  }

  private void checkAttendanceCode(Seminar seminar, String attendanceCode, String key) {
    String seminarAttendanceCode = seminar.getAttendanceCode();

    if (!seminarAttendanceCode.equals(attendanceCode)) {
      String attemptNumber = redisUtil.getData(key, String.class).orElseThrow();
      throw new BusinessException(attemptNumber, "attemptNumber",
          SEMINAR_ATTENDANCE_CODE_NOT_AVAILABLE);
    }
  }

  private void checkDuplicateAttendance(SeminarAttendance seminarAttendance) {
    if (seminarAttendance.getSeminarAttendanceStatus().getType() != BEFORE_ATTENDANCE) {
      throw new BusinessException(seminarAttendance.getSeminarAttendanceStatus(),
          "attendanceStatus",
          SEMINAR_ATTENDANCE_DUPLICATE);
    }
  }

  @Transactional
  public void changeStatus(long attendanceId, SeminarAttendanceStatusType statusType,
      String excuse) {
    SeminarAttendance seminarAttendance = attendanceRepository.findById(attendanceId)
        .orElseThrow(() -> new BusinessException(attendanceId, "attendanceId",
            SEMINAR_ATTENDANCE_NOT_FOUND));
    if (statusType == LATENESS || statusType == PERSONAL) {
      seminarAttendance.changeStatus(statusType, excuse);
      return;
    }
    seminarAttendance.changeStatus(statusType);
    seminarAttendance.removeExcuse();
  }

  @Scheduled(cron = "0 50 23 * * ?", zone = "Asia/Seoul") // 매일 23시 50분에 실행
  @Transactional
  public void changeAllBeforeAttendanceToAbsence() {
    attendanceRepository.updateAllBeforeAttendanceToAbsence();
  }

  public Page<SeminarAttendanceManageResponse> getAttendances(Pageable pageable) {
    Page<Member> regulars = memberFindService.findAllRegular(pageable);
    LocalDate now = LocalDate.now();
    LocalDate semesterFirstDate = SemesterUtil.getSemesterFirstDate(now);

    return regulars.map(member -> {
      var seminarAttendances = attendanceRepository.findAllRecentByMember(member.getId(),
          semesterFirstDate);
      return SeminarAttendanceManageResponse.of(member, seminarAttendances);
    });
  }
}
