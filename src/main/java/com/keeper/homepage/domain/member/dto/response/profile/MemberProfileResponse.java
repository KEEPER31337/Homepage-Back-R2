package com.keeper.homepage.domain.member.dto.response.profile;


import static java.util.stream.Collectors.*;
import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.friend.Friend;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class MemberProfileResponse {

  private long id;
  private String emailAddress;
  private String realName;
  private LocalDate birthday;
  private String thumbnailPath;
  private String studentId;
  private String generation;
  private int point;
  private String memberType;
  private List<String> memberJobs;
  private List<MemberFriendResponse> follower;
  private List<MemberFriendResponse> followee;

  public static MemberProfileResponse of(Member member, Member me) {
    return MemberProfileResponse.builder()
        .id(member.getId())
        .emailAddress(member.getProfile().getEmailAddress().get())
        .realName(member.getRealName())
        .birthday(member.getProfile().getBirthday())
        .thumbnailPath(member.getThumbnailPath())
        .studentId(getStudentIdIfMine(member, me))
        .generation(member.getGeneration())
        .point(member.getPoint())
        .memberType(member.getMemberType().getType().name())
        .memberJobs(member.getJobs())
        .follower(getFollower(member))
        .followee(getFollowee(member))
        .build();
  }

  private static String getStudentIdIfMine(Member member, Member me) {
    return Objects.equals(member.getId(), me.getId()) ? member.getProfile().getStudentId().get() : "default";
  }

  private static List<MemberFriendResponse> getFollower(Member member) {
    return member.getFollowee().stream()
        .map(Friend::getFollower)
        .map(MemberFriendResponse::from)
        .toList();
  }

  private static List<MemberFriendResponse> getFollowee(Member member) {
    return member.getFollower().stream()
        .map(Friend::getFollowee)
        .map(MemberFriendResponse::from)
        .toList();
  }
}
