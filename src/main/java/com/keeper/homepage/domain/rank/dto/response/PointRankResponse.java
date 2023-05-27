package com.keeper.homepage.domain.rank.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class PointRankResponse {

  private String nickName;
  private Float generation;
  private Integer point;

  public static PointRankResponse from(Member member) {
    return PointRankResponse.builder()
        .nickName(member.getNickname())
        .generation(member.getGeneration())
        .point(member.getPoint())
        .build();
  }
}
