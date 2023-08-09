package com.keeper.homepage.domain.merit.dto.response;

import com.keeper.homepage.domain.merit.entity.MeritLog;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchMeritLogListResponse {

  private Long id;
  private LocalDateTime giveTime;
  private String awarderName;
  private Float awarderGeneration;
  private Integer score;
  private Long meritTypeId;
  private String reason;

  public static SearchMeritLogListResponse from(MeritLog meritLog) {
    return SearchMeritLogListResponse.builder()
        .id(meritLog.getId())
        .giveTime(meritLog.getTime())
        .awarderName(meritLog.getAwarder().getRealName())
        .awarderGeneration(meritLog.getGiver().getGeneration())
        .score(meritLog.getMeritType().getMerit())
        .meritTypeId(meritLog.getMeritType().getId())
        .reason(meritLog.getMeritType().getDetail())
        .build();
  }

}
