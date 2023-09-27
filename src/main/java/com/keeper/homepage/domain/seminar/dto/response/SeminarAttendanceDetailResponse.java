package com.keeper.homepage.domain.seminar.dto.response;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PACKAGE)
public class SeminarAttendanceDetailResponse {

  private Long seminarId;
  private Long attendanceId;
  private String attendanceStatus;
  private String excuse;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDateTime attendDate;

  public static SeminarAttendanceDetailResponse from(SeminarAttendance seminarAttendance) {
    return SeminarAttendanceDetailResponse.builder()
        .seminarId(seminarAttendance.getSeminar().getId())
        .attendanceId(seminarAttendance.getId())
        .attendanceStatus(seminarAttendance.getSeminarAttendanceStatus().getType().toString())
        .excuse(seminarAttendance.getSeminarAttendanceExcuse() != null ?
            seminarAttendance.getSeminarAttendanceExcuse().getAbsenceExcuse() : null)
        .attendDate(seminarAttendance.getAttendTime())
        .build();
  }
}
