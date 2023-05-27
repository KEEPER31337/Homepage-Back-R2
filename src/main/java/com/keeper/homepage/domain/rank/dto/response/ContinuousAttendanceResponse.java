package com.keeper.homepage.domain.rank.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.attendance.entity.Attendance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class ContinuousAttendanceResponse {

  private String nickname;
  private Float generation;
  private Integer continuousDay;
  private String thumbnailPath;

  public static ContinuousAttendanceResponse from(Attendance attendance) {
    return ContinuousAttendanceResponse.builder()
        .nickname(attendance.getMember().getNickname())
        .generation(attendance.getMember().getGeneration())
        .continuousDay(attendance.getContinuousDay())
        .thumbnailPath(attendance.getMember().getThumbnailPath())
        .build();
  }
}
