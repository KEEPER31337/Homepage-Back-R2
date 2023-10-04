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
public class AttendanceTodayRankResponse {

  private Integer rank;
  private String thumbnailPath;
  private String realName;
  private String generation;
  private Integer totalAttendance;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime time;

  public static AttendanceTodayRankResponse from(Attendance attendance) {
    return AttendanceTodayRankResponse.builder()
        .rank(attendance.getRank())
        .thumbnailPath(attendance.getMember().getThumbnailPath())
        .realName(attendance.getMember().getRealName())
        .generation(attendance.getMember().getGeneration())
        .totalAttendance(attendance.getMember().getTotalAttendance())
        .time(attendance.getTime())
        .build();
  }

}
