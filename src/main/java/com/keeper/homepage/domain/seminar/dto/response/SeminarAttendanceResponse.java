package com.keeper.homepage.domain.seminar.dto.response;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PACKAGE)
public class SeminarAttendanceResponse {

  private Long id;
  private SeminarAttendanceStatusType statusType;

  public static SeminarAttendanceResponse of(SeminarAttendance attendance) {
    return SeminarAttendanceResponse.builder()
        .id(attendance.getId())
        .statusType(attendance.getSeminarAttendanceStatus().getType())
        .build();
  }
}
