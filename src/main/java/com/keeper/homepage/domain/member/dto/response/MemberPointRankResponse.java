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

  private Long memberId;
  private String realName;
  private String generation;
  private Integer point;
  private String thumbnailPath;

  public static MemberPointRankResponse from(Member member) {
    return MemberPointRankResponse.builder()
        .memberId(member.getId())
        .realName(member.getRealName())
        .generation(member.getGeneration())
        .point(member.getPoint())
        .thumbnailPath(member.getThumbnailPath())
        .build();
  }
}
