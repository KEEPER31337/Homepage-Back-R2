package com.keeper.homepage.domain.attendance.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.attendance.entity.Attendance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class AttendanceInfoResponse {

  private Integer continuousDay;
  private Integer todayRank;
  private Integer todayPoint;

  public static AttendanceInfoResponse from(Attendance attendance) {
    return AttendanceInfoResponse.builder()
        .continuousDay(attendance.getContinuousDay())
        .todayRank(attendance.getRank())
        .todayPoint(attendance.getTotalPoint())
        .build();
  }
}
