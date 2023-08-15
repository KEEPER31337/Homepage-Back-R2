package com.keeper.homepage.domain.member.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class MemberPointRankResponse {

  private String realName;
  private String generation;
  private Integer point;

  public static MemberPointRankResponse from(Member member) {
    return MemberPointRankResponse.builder()
        .realName(member.getRealName())
        .generation(member.getGeneration())
        .point(member.getPoint())
        .build();
  }
}
