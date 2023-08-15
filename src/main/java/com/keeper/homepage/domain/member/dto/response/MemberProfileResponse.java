package com.keeper.homepage.domain.member.dto.response;


import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.EmailAddress;
import com.keeper.homepage.domain.member.entity.embedded.Generation;
import com.keeper.homepage.domain.member.entity.embedded.Profile;
import com.keeper.homepage.domain.member.entity.embedded.RealName;
import com.keeper.homepage.domain.member.entity.embedded.StudentId;
import com.keeper.homepage.domain.member.entity.friend.Friend;
import com.keeper.homepage.domain.member.entity.job.MemberHasMemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import com.keeper.homepage.domain.member.entity.type.MemberType;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class MemberProfileResponse {

  private long id;
  private EmailAddress emailAddress;
  private RealName realName;
  private LocalDate birthday;
  private StudentId studentId;
  private String thumbnailPath;
  private Float generation;
  private int point;
  private MemberType memberType;
  private Set<MemberHasMemberJob> memberJob;

  public static MemberProfileResponse from(Profile profile, Member member) {
    return MemberProfileResponse.builder()
        .id(member.getId())
        .emailAddress(profile.getEmailAddress())
        .realName(profile.getRealName())
        .birthday(profile.getBirthday())
        .studentId(profile.getStudentId())
        .thumbnailPath(profile.getThumbnail().getPath())
        .generation(member.getGeneration())
        .point(member.getPoint())
        .memberType(member.getMemberType())
        .memberJob(member.getMemberJob())
        .build();
  }
}
