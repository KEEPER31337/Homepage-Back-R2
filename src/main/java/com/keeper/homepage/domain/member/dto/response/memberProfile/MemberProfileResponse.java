package com.keeper.homepage.domain.member.dto.response.memberProfile;


import static java.util.stream.Collectors.*;
import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.friend.Friend;
import com.keeper.homepage.domain.member.entity.job.MemberHasMemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType;
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
  private String studentId;
  private String thumbnailPath;
  private String generation;
  private int point;
  private String memberType;
  private List<String> memberJobs;
  private List<MemberFollowResponse> follower;
  private List<MemberFollowResponse> followee;

  public static MemberProfileResponse from(Member member) {
    return MemberProfileResponse.builder()
        .id(member.getId())
        .emailAddress(member.getEmailAddress())
        .realName(member.getRealName())
        .birthday(member.getBirthday())
        .studentId(member.getStudentId())
        .thumbnailPath(member.getThumbnailPath())
        .generation(member.getGeneration())
        .point(member.getPoint())
        .memberType(member.getMemberType().getType().name())
        .memberJobs(getJobs(member))
        .follower(getFollower(member))
        .followee(getFollowee(member))
        .build();
  }

  private static List<String> getJobs(Member member) {
    return member.getMemberJob().stream()
        .map(MemberHasMemberJob::getMemberJob)
        .map(MemberJob::getType)
        .map(MemberJobType::name)
        .collect(toList());
  }

  private static List<MemberFollowResponse> getFollower(Member member) {
    return member.getFriends().stream()
        .map(Friend::getFollower)
        .map(MemberFollowResponse::from)
        .collect(toList());
  }

  private static List<MemberFollowResponse> getFollowee(Member member) {
    return member.getFriends().stream()
        .map(Friend::getFollowee)
        .map(MemberFollowResponse::from)
        .collect(toList());
  }


}
