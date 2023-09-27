package com.keeper.homepage.domain.study.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class StudyMemberResponse {

  private Long memberId;
  private String generation;
  private String realName;

  public static StudyMemberResponse from(Member member) {
    return StudyMemberResponse.builder()
        .memberId(member.getId())
        .generation(member.getGeneration())
        .realName(member.getRealName())
        .build();
  }
}
