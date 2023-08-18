package com.keeper.homepage.domain.member.dto.response.profile;


import static java.util.stream.Collectors.*;
import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.friend.Friend;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class MemberProfileResponse {

  private long id;
  private String emailAddress;
  private String realName;
  private LocalDate birthday;
  private String thumbnailPath;
  private String generation;
  private int point;
  private String memberType;
  private List<String> memberJobs;
  private List<MemberFriendResponse> follower;
  private List<MemberFriendResponse> followee;

  public static MemberProfileResponse of(Member member, List<Friend> friends) {
    return MemberProfileResponse.builder()
        .id(member.getId())
        .emailAddress(member.getProfile().getEmailAddress().get())
        .realName(member.getRealName())
        .birthday(member.getProfile().getBirthday())
        .thumbnailPath(member.getThumbnailPath())
        .generation(member.getGeneration())
        .point(member.getPoint())
        .memberType(member.getMemberType().getType().name())
        .memberJobs(member.getJobs())
        .follower(getFollower(friends))
        .followee(getFollowee(member))
        .build();
  }

  private static List<MemberFriendResponse> getFollower(List<Friend> friends) {
    return friends.stream()
        .map(Friend::getFollower)
        .map(MemberFriendResponse::from)
        .collect(toList());
  }

  private static List<MemberFriendResponse> getFollowee(Member member) {
    return member.getFollower().stream()
        .map(Friend::getFollowee)
        .map(MemberFriendResponse::from)
        .collect(toList());
  }
}
