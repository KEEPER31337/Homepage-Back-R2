package com.keeper.homepage.domain.merit.dto.response;

import com.keeper.homepage.domain.merit.entity.MeritType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MeritTypeResponse {

  private long id;
  private int score;
  private String detail;
  private Boolean isMerit;

  public static MeritTypeResponse from(MeritType meritType) {
    return MeritTypeResponse.builder()
        .id(meritType.getId())
        .score(meritType.getMerit())
        .detail(meritType.getDetail())
        .isMerit(meritType.getIsMerit())
        .build();
  }

}
