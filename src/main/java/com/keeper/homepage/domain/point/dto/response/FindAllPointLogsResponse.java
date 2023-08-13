package com.keeper.homepage.domain.point.dto.response;

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
public class FindAllPointLogsResponse {

  private int point;
  private String description;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDateTime date;

  public static FindAllPointLogsResponse from(PointLog pointLog) {
    return FindAllPointLogsResponse.builder()
        .point(pointLog.getPoint())
        .description(pointLog.getDetail())
        .date(pointLog.getTime())
        .build();

  }
}
