package com.keeper.homepage.domain.vote.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record VoteReceiptCheckRequest(
    @JsonProperty("receipt_token")
    @NotNull(message = "영수증 토큰을 입력해주세요.")
    UUID receiptToken
) {

  @JsonIgnore
  @AssertTrue(message = "영수증 토큰은 UUID v4 형식이어야 합니다.")
  public boolean isReceiptTokenV4() {
    return receiptToken == null
        || (receiptToken.version() == 4 && receiptToken.variant() == 2);
  }
}
