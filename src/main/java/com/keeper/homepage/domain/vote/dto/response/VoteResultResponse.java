package com.keeper.homepage.domain.vote.dto.response;

import com.keeper.homepage.domain.vote.entity.VoteParticipation;
import com.keeper.homepage.domain.vote.entity.VoteReceipt;
import java.util.Comparator;
import java.util.List;

public record VoteResultResponse(
    List<VoteResultParticipationResponse> participations,
    List<VoteResultReceiptResponse> receiptTokenChoices,
    VoteDetailResponse vote
) {

  public static VoteResultResponse from(
      List<VoteParticipation> participations,
      List<VoteReceipt> receipts,
      VoteDetailResponse vote
  ) {
    List<VoteResultParticipationResponse> participationResponses = participations.stream()
        .sorted(Comparator
            .comparing(VoteParticipation::getVoterNameSnapshot)
            .thenComparing(VoteParticipation::getVoterGenerationSnapshot))
        .map(VoteResultParticipationResponse::from)
        .toList();
    List<VoteResultReceiptResponse> receiptResponses = receipts.stream()
        .sorted(Comparator.comparing(receipt -> receipt.getToken().toString()))
        .map(VoteResultReceiptResponse::from)
        .toList();
    return new VoteResultResponse(participationResponses, receiptResponses, vote);
  }
}
