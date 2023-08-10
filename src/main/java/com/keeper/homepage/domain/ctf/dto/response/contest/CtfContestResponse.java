package com.keeper.homepage.domain.ctf.dto.response.contest;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.ctf.entity.CtfContest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class CtfContestResponse {

  private Long id;
  private String name;
  private String description;
  private String creatorName;
  private boolean isJoinable;

  public static CtfContestResponse from(CtfContest ctfContest) {
    return CtfContestResponse.builder()
        .id(ctfContest.getId())
        .name(ctfContest.getName())
        .description(ctfContest.getDescription())
        .creatorName(ctfContest.getCreator().getRealName())
        .isJoinable(ctfContest.getIsJoinable())
        .build();
  }
}
