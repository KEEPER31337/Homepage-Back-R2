package com.keeper.homepage.domain.vote.dto.response;

import com.keeper.homepage.domain.vote.entity.VoteAgenda;
import java.util.List;

public record VoteAgendaResponse(
    Long id,
    String title,
    int minSelect,
    int maxSelect,
    List<VoteOptionResponse> options
) {

  public static VoteAgendaResponse from(VoteAgenda agenda, List<VoteOptionResponse> options) {
    return new VoteAgendaResponse(
        agenda.getId(),
        agenda.getTitle(),
        agenda.getMinSelect(),
        agenda.getMaxSelect(),
        options
    );
  }
}
