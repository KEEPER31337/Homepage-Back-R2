package com.keeper.homepage.domain.merit.dto.response;

import lombok.Getter;

@Getter
public class MeritLogsGroupByMemberResponse {

  private Long memberId;
  private String memberName;
  private String generation;
  private Integer merit;
  private Integer demerit;

  public MeritLogsGroupByMemberResponse(Long memberId, String memberName, String generation,
      Integer merit, Integer demerit) {
    this.memberId = memberId;
    this.memberName = memberName;
    this.generation = generation;
    this.merit = merit;
    this.demerit = demerit;
  }

}
