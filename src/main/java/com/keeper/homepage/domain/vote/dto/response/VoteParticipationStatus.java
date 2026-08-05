package com.keeper.homepage.domain.vote.dto.response;

/**
 * 1: 투표 권한 있음, 2: 투표 권한 없음, 3: 제출 완료, 4: 투표 기간 아님
 */
public enum VoteParticipationStatus {
  PERMITTED(1),
  NOT_PERMITTED(2),
  SUBMITTED(3),
  OUTSIDE_VOTING_PERIOD(4);

  private final int code;

  VoteParticipationStatus(int code) {
    this.code = code;
  }

  public int code() {
    return code;
  }
}
