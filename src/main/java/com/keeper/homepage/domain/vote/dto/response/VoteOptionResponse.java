package com.keeper.homepage.domain.vote.dto.response;

import com.keeper.homepage.domain.vote.entity.VoteOption;

public record VoteOptionResponse(
    Long id,
    String content
) {

  public static VoteOptionResponse from(VoteOption option) {
    return new VoteOptionResponse(option.getId(), option.getContent());
  }
}
