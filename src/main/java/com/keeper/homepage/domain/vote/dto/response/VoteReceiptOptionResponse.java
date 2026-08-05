package com.keeper.homepage.domain.vote.dto.response;

import com.keeper.homepage.domain.vote.entity.VoteOption;

public record VoteReceiptOptionResponse(
    Long optionId,
    String content
) {

  public static VoteReceiptOptionResponse from(VoteOption option) {
    return new VoteReceiptOptionResponse(option.getId(), option.getContent());
  }
}
