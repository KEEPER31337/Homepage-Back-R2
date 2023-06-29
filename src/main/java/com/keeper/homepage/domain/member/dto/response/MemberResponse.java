package com.keeper.homepage.domain.member.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class MemberResponse {

  private Long memberId;
  private String memberName;
  private Float generation;

  public static MemberResponse from(Member member) {
    return MemberResponse.builder()
        .memberId(member.getId())
        .memberName(member.getRealName())
        .generation(member.getGeneration())
        .build();
  }
}
