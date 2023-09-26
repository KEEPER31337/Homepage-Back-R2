package com.keeper.homepage.domain.member.dto.response.profile;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.Profile;
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
  private String generation;

  public static MemberFriendResponse from(Member member) {
    return MemberFriendResponse.builder()
        .id(member.getId())
        .name(member.getRealName())
        .thumbnailPath(member.getThumbnailPath())
        .generation(member.getGeneration())
        .build();
  }

}
