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
public class SearchMemberMeritLogResponse {

  private long id;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime giveTime;
  private int score;
  private long meritTypeId;
  private Boolean isMerit;
  private String reason;

  public static SearchMemberMeritLogResponse from(MeritLog meritLog) {
    return SearchMemberMeritLogResponse.builder()
        .id(meritLog.getId())
        .giveTime(meritLog.getTime())
        .score(meritLog.getMeritType().getMerit())
        .meritTypeId(meritLog.getMeritType().getId())
        .isMerit(meritLog.getMeritType().getIsMerit())
        .reason(meritLog.getMeritType().getDetail())
        .build();
  }
}
