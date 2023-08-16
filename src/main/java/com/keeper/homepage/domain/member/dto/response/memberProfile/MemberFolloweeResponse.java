package com.keeper.homepage.domain.member.dto.response.memberProfile;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.friend.Friend;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class MemberFolloweeResponse {

  private Long id;
  private String name;
  private String thumbnailPath;

  public static MemberFolloweeResponse from(Friend friend) {
    return MemberFolloweeResponse.builder()
        .id(friend.getFollowee().getId())
        .name(friend.getFollowee().getRealName())
        .thumbnailPath(friend.getFollowee().getThumbnailPath())
        .build();
  }

}
