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

  private Long id;
  private Integer score;
  private String detail;

  public static MeritTypeResponse from(MeritType meritType) {
    return MeritTypeResponse.builder()
        .id(meritType.getId())
        .score(meritType.getMerit())
        .detail(meritType.getDetail())
        .build();
  }

}
