package com.keeper.homepage.domain.vote.dto.response;

import static com.keeper.homepage.domain.member.application.convenience.MemberFindService.VIRTUAL_MEMBER_ID;

import com.keeper.homepage.domain.vote.entity.VoteParticipation;

public record VoteResultParticipationResponse(
    String realName,
    String generation
) {

  private static final String WITHDRAWN_MEMBER_SUFFIX = " (홈페이지 탈퇴한 회원)";

  public static VoteResultParticipationResponse from(VoteParticipation participation) {
    String realName = participation.getVoterNameSnapshot();
    if (participation.getMember().getId() == VIRTUAL_MEMBER_ID) {
      realName += WITHDRAWN_MEMBER_SUFFIX;
    }
    return new VoteResultParticipationResponse(
        realName,
        Float.toString(participation.getVoterGenerationSnapshot())
    );
  }
}
