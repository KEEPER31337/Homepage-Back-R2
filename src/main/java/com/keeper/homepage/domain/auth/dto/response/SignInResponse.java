package com.keeper.homepage.domain.auth.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.Member;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class SignInResponse {

  private long id;
  private String emailAddress;
  private String realName;
  private LocalDate birthday;
  private String studentId;
  private String thumbnailPath;
  private String generation;
  private int point;
  private int level;
  private int totalAttendance;
  private String memberType;
  private String memberRank;
  private List<String> memberJobs;

  public static SignInResponse of(Member member, List<String> roles) {
    return new SignInResponse(
        member.getId(),
        member.getProfile().getEmailAddress().get(),
        member.getRealName(),
        member.getProfile().getBirthday(),
        member.getProfile().getStudentId().get(),
        member.getThumbnailPath(),
        member.getGeneration(),
        member.getPoint(),
        member.getLevel(),
        member.getTotalAttendance(),
        member.getMemberType().getType().name(),
        member.getMemberRank().getType().name(),
        roles);
  }

}
