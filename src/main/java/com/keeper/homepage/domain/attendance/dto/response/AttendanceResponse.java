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

  private Long id;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate date;

  public static AttendanceResponse from(Attendance attendance) {
    return AttendanceResponse.builder()
        .id(attendance.getId())
        .date(attendance.getDate())
        .build();
  }
}
