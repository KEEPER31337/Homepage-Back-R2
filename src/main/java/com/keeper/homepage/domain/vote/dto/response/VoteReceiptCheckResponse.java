package com.keeper.homepage.domain.vote.dto.response;

import java.util.List;

public record VoteReceiptCheckResponse(
    Long voteId,
    List<VoteReceiptSelectionResponse> selections
) {

}
