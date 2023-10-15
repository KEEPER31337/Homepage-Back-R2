package com.keeper.homepage.domain.attendance.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.attendance.entity.Attendance;
import com.keeper.homepage.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class AttendanceInfoResponse {

  private Integer totalAttendance;
  private Integer continuousDay;
  private Integer todayRank;
  private Integer todayPoint;

  public static AttendanceInfoResponse of(Member member, Attendance attendance) {
    return AttendanceInfoResponse.builder()
        .totalAttendance(member.getTotalAttendance())
        .continuousDay(attendance.getContinuousDay())
        .todayRank(attendance.getRank())
        .todayPoint(attendance.getTotalPoint())
        .build();
  }

  public static AttendanceInfoResponse recent(Member member, Attendance attendance) {
    return AttendanceInfoResponse.builder()
        .totalAttendance(member.getTotalAttendance())
        .continuousDay(attendance.getContinuousDay())
        .todayRank(null)
        .todayPoint(0)
        .build();
  }
}
