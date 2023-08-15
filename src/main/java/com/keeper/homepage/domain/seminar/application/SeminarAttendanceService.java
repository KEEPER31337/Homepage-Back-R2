package com.keeper.homepage.domain.seminar.application;

import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.BEFORE_ATTENDANCE;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_ATTEMPT_NOT_AVAILABLE;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_CODE_NOT_AVAILABLE;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_DUPLICATE;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_UNABLE;

import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.seminar.application.convenience.ValidSeminarFindService;
import com.keeper.homepage.domain.seminar.dao.SeminarAttendanceRepository;
import com.keeper.homepage.domain.seminar.dto.request.SeminarAttendanceCodeRequest;
import com.keeper.homepage.domain.seminar.dto.request.SeminarAttendanceStatusRequest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarAttendanceResponse;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.redis.RedisUtil;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
  public SeminarAttendanceResponse attendance(Long seminarId, Member member, SeminarAttendanceCodeRequest request) {
    Seminar seminar = validSeminarFindService.findById(seminarId);
    SeminarAttendance seminarAttendance = attendanceRepository.findBySeminarAndMember(seminar, member)
        .orElseThrow(() -> new BusinessException(member.getRealName(), "member", SEMINAR_ATTENDANCE_UNABLE));

    String key = "seminar:" + LocalDate.now() + ":memberId:" + member.getId();
    checkAttemptNumberLimit(key);

    validAttendanceCode(seminar, request, key);
    checkDuplicateAttendance(seminarAttendance);

    seminarAttendance.changeStatus(seminar.getStatus().getType());
    return SeminarAttendanceResponse.of(seminarAttendance);
  }

  private void checkAttemptNumberLimit(String key) {
    int attemptNumber = redisUtil.increaseAndGetWithExpire(key, ONE_HOUR).intValue();
    if (attemptNumber > MAX_ATTEMPT_COUNT) {
      throw new BusinessException(attemptNumber, "attemptNumber", SEMINAR_ATTENDANCE_ATTEMPT_NOT_AVAILABLE);
    }
  }

  private void validAttendanceCode(Seminar seminar, SeminarAttendanceCodeRequest request, String key) {
    String seminarAttendanceCode = seminar.getAttendanceCode();
    String inputAttendanceCode = request.attendanceCode();

    if (!seminarAttendanceCode.equals(inputAttendanceCode)) {
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

  @Transactional
  public void changeStatus(long seminarId, long memberId, SeminarAttendanceStatusRequest request) {
    Member member = memberFindService.findById(memberId);
    Seminar seminar = validSeminarFindService.findById(seminarId);

    SeminarAttendance seminarAttendance = attendanceRepository.findBySeminarAndMember(seminar, member)
        .orElseThrow(() -> new BusinessException(member.getRealName(), "realName", MEMBER_NOT_FOUND));

    seminarAttendance.changeStatus(request.excuse(), request.statusType());
  }
}
