package com.keeper.homepage.domain.point.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.keeper.homepage.domain.point.entity.PointLog;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FindAllPointLogResponse {

  private int point;
  private String description;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime date;

  public static FindAllPointLogResponse from(PointLog pointLog) {
    return FindAllPointLogResponse.builder()
        .point(pointLog.getPoint())
        .description(pointLog.getDetail())
        .date(pointLog.getTime())
        .build();
  }
}
