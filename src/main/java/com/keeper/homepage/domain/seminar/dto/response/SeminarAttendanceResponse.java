package com.keeper.homepage.domain.seminar.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class SeminarAttendanceResponse {

  private Long id;
  private SeminarAttendanceStatusType attendanceStatus;

  public SeminarAttendanceResponse(SeminarAttendance seminarAttendance) {
    this.id = seminarAttendance.getId();
    this.attendanceStatus = seminarAttendance.getSeminarAttendanceStatus().getType();
  }
}
