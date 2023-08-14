package com.keeper.homepage.domain.attendance.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.keeper.homepage.domain.attendance.entity.Attendance;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class AttendanceRankResponse {

  private Integer rank;
  private String thumbnailPath;
  private String realName;
  private String generation;
  private Integer continuousDay;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime time;

  public static AttendanceRankResponse from(Attendance attendance) {
    return AttendanceRankResponse.builder()
        .rank(attendance.getRank())
        .thumbnailPath(attendance.getMember().getThumbnailPath())
        .realName(attendance.getMember().getRealName())
        .generation(attendance.getMember().getGeneration())
        .continuousDay(attendance.getContinuousDay())
        .time(attendance.getTime())
        .build();
  }
}
