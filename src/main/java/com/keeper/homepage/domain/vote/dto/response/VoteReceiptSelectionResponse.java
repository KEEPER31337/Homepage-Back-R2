package com.keeper.homepage.domain.vote.dto.response;

import com.keeper.homepage.domain.vote.entity.VoteAgenda;
import java.util.List;

public record VoteReceiptSelectionResponse(
    Long agendaId,
    String agendaTitle,
    List<VoteReceiptOptionResponse> options
) {

  public static VoteReceiptSelectionResponse from(
      VoteAgenda agenda,
      List<VoteReceiptOptionResponse> options
  ) {
    return new VoteReceiptSelectionResponse(agenda.getId(), agenda.getTitle(), options);
  }
}
