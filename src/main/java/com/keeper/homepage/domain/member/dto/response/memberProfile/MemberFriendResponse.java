package com.keeper.homepage.domain.member.dto.response.memberProfile;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class MemberFriendResponse {

  private Long id;
  private String name;
  private String thumbnailPath;

  public static MemberFriendResponse from(Member member) {
    return MemberFriendResponse.builder()
        .id(member.getId())
        .name(member.getRealName())
        .thumbnailPath(member.getThumbnailPath())
        .build();
  }

}
