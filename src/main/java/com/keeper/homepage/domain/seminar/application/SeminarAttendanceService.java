package com.keeper.homepage.domain.seminar.application;

import static com.keeper.homepage.global.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_CODE_NOT_AVAILABLE;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_DUPLICATE;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_NOT_FOUND;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.Nickname;
import com.keeper.homepage.domain.seminar.dao.SeminarAttendanceRepository;
import com.keeper.homepage.domain.seminar.dao.SeminarRepository;
import com.keeper.homepage.domain.seminar.dto.request.SeminarAttendanceCodeRequest;
import com.keeper.homepage.domain.seminar.dto.request.SeminarAttendanceStatusRequest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarAttendanceResponse;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import com.keeper.homepage.global.error.BusinessException;
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
  public SeminarAttendanceResponse save(Long seminarId, Member member, SeminarAttendanceCodeRequest request){
    Seminar seminar = seminarRepository.findById(seminarId)
        .orElseThrow(() -> new BusinessException(seminarId, "seminarId", SEMINAR_NOT_FOUND));

    validAttendanceCode(seminar, request);
    checkExistsAttendanceSeminar(seminar, member);

    SeminarAttendance attendance = SeminarAttendance.builder()
        .seminar(seminar)
        .member(member)
        .seminarAttendanceStatus(seminar.getStatus())
        .build();

    return SeminarAttendanceResponse.from(attendanceRepository.save(attendance));
  }

  private void validAttendanceCode(Seminar seminar, SeminarAttendanceCodeRequest request) {
    String seminarAttendanceCode = seminar.getAttendanceCode();
    String inputAttendanceCode = request.attendanceCode();

    if (!seminarAttendanceCode.equals(inputAttendanceCode)) {
      throw new BusinessException(inputAttendanceCode, "inputAttendanceCode",
          SEMINAR_ATTENDANCE_CODE_NOT_AVAILABLE);
    }
  }

  private void checkExistsAttendanceSeminar(Seminar seminar, Member member) {
    if (attendanceRepository.existsBySeminarAndMember(seminar, member)) {
      throw new BusinessException(seminar.getName(), "seminar", SEMINAR_ATTENDANCE_DUPLICATE);
    }
  }

  @Transactional
  public void changeStatus(Long seminarId, Member member, SeminarAttendanceStatusRequest request) {
    Nickname nickname = member.getProfile().getNickname();

    Seminar seminar = seminarRepository.findById(seminarId)
        .orElseThrow(() -> new BusinessException(seminarId, "seminarId", SEMINAR_NOT_FOUND));

    SeminarAttendance seminarAttendance = attendanceRepository.findBySeminarAndMember(seminar, member)
        .orElseThrow(() -> new BusinessException(nickname, "nickname", MEMBER_NOT_FOUND));

    seminarAttendance.changeStatus(request.excuse(), request.statusType());
  }
}
