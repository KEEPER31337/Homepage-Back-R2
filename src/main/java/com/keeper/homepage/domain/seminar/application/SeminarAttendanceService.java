package com.keeper.homepage.domain.seminar.application;

import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.ABSENCE;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.ATTENDANCE;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.LATENESS;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.getSeminarAttendanceStatusBy;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_CODE_NOT_AVAILABLE;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_DUPLICATE;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_NOT_FOUND;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.seminar.dao.SeminarAttendanceRepository;
import com.keeper.homepage.domain.seminar.dao.SeminarRepository;
import com.keeper.homepage.domain.seminar.dto.request.SeminarAttendanceRequest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarAttendanceResponse;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus;
import com.keeper.homepage.global.error.BusinessException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeminarAttendanceService {

  private final SeminarAttendanceRepository attendanceRepository;
  private final SeminarRepository seminarRepository;

  @Transactional
  public SeminarAttendanceResponse save(Member member, SeminarAttendanceRequest request){
    Long seminarId = request.id();
    Seminar seminar = seminarRepository.findById(seminarId)
        .orElseThrow(() -> new BusinessException(seminarId, "seminarId", SEMINAR_NOT_FOUND));

    validAttendanceCode(seminar, request);
    existsAttendanceSeminar(seminar, member);

    SeminarAttendance attendance = SeminarAttendance.builder()
        .seminar(seminar)
        .member(member)
        .seminarAttendanceStatus(getStatus(seminar))
        .build();

    return new SeminarAttendanceResponse(attendanceRepository.save(attendance));
  }

  private void validAttendanceCode(Seminar seminar, SeminarAttendanceRequest request) {
    String seminarAttendanceCode = seminar.getAttendanceCode();
    String inputAttendanceCode = request.attendanceCode();

    if (!seminarAttendanceCode.equals(inputAttendanceCode)) {
      throw new BusinessException(inputAttendanceCode, "inputAttendanceCode",
          SEMINAR_ATTENDANCE_CODE_NOT_AVAILABLE);
    }
  }

  private void existsAttendanceSeminar(Seminar seminar, Member member) {
    if (attendanceRepository.existsBySeminarEqualsAndMemberEquals(seminar, member)) {
      throw new BusinessException(seminar.getName(), "seminar", SEMINAR_ATTENDANCE_DUPLICATE);
    }
  }

  private SeminarAttendanceStatus getStatus(Seminar seminar) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime attendanceCloseTime = seminar.getAttendanceCloseTime();
    LocalDateTime latenessCloseTime = seminar.getLatenessCloseTime();

    if (now.isBefore(attendanceCloseTime)) {
      return getSeminarAttendanceStatusBy(ATTENDANCE);
    }

    if (now.isAfter(attendanceCloseTime) && now.isBefore(latenessCloseTime)) {
      return getSeminarAttendanceStatusBy(LATENESS);
    }

    return getSeminarAttendanceStatusBy(ABSENCE);
  }
}
