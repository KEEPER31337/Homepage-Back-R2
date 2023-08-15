package com.keeper.homepage.domain.merit.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.keeper.homepage.domain.merit.entity.MeritLog;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchMeritLogListResponse {

  private long id;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime giveTime;
  private String awarderName;
  private String awarderGeneration;
  private int score;
  private long meritTypeId;
  private String reason;

  public static SearchMeritLogListResponse from(MeritLog meritLog) {
    return SearchMeritLogListResponse.builder()
        .id(meritLog.getId())
        .giveTime(meritLog.getTime())
        .awarderName(meritLog.getMemberRealName())
        .awarderGeneration(meritLog.getMemberGeneration())
        .score(meritLog.getMeritType().getMerit())
        .meritTypeId(meritLog.getMeritType().getId())
        .reason(meritLog.getMeritType().getDetail())
        .build();
  }

}
