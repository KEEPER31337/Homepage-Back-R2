package com.keeper.homepage.domain.attendance.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.keeper.homepage.domain.attendance.entity.Attendance;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class AttendanceResponse {

  private int value;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate day;

  public static AttendanceResponse from(Attendance attendance) {
    return AttendanceResponse.builder()
        .value(1)
        .day(attendance.getDate())
        .build();
  }
}
