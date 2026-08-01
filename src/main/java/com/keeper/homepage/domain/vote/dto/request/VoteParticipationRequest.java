package com.keeper.homepage.domain.vote.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record VoteParticipationRequest(
    @JsonProperty("receipt_token")
    @NotNull(message = "영수증 토큰을 입력해주세요.")
    UUID receiptToken,

    @NotEmpty(message = "안건별 선택 정보를 한 개 이상 입력해주세요.")
    List<@NotNull(message = "선택 정보는 null일 수 없습니다.")
        @Valid VoteSelectionRequest> selections
) {

  @JsonIgnore
  @AssertTrue(message = "영수증 토큰은 UUID v4 형식이어야 합니다.")
  public boolean isReceiptTokenV4() {
    return receiptToken == null
        || (receiptToken.version() == 4 && receiptToken.variant() == 2);
  }
}
