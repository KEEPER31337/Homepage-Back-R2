package com.keeper.homepage.domain.attendance.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.attendance.entity.Attendance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class AttendancePointResponse {

  private Integer point;
  private Integer continuousPoint;
  private Integer rankPoint;
  private Integer randomPoint;

  public static AttendancePointResponse from(Attendance attendance) {
    return AttendancePointResponse.builder()
        .point(attendance.getPoint())
        .continuousPoint(attendance.getContinuousPoint())
        .rankPoint(attendance.getRankPoint())
        .randomPoint(attendance.getRandomPoint())
        .build();
  }
}
