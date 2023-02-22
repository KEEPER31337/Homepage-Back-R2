package com.keeper.homepage.domain.seminar.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class SeminarAttendanceResponse {

  private Long id;
  private SeminarAttendanceStatusType statusType;

  public static SeminarAttendanceResponse from(SeminarAttendance attendance) {
    return new SeminarAttendanceResponse(attendance.getId(),
        attendance.getSeminarAttendanceStatus().getType());
  }
}
