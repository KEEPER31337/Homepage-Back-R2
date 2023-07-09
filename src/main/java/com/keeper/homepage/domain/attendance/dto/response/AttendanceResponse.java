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
public class AttendanceResponse {

  private String thumbnailPath;
  private String nickName;
  private Float generation;
  private Integer continuousDay;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime time;

  public static AttendanceResponse from(Attendance attendance) {
    return AttendanceResponse.builder()
        .thumbnailPath(attendance.getMember().getThumbnailPath())
        .nickName(attendance.getMember().getNickname())
        .generation(attendance.getMember().getGeneration())
        .continuousDay(attendance.getContinuousDay())
        .time(attendance.getTime())
        .build();
  }
}
