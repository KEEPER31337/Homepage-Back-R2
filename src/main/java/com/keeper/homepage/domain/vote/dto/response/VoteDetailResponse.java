package com.keeper.homepage.domain.vote.dto.response;

import com.keeper.homepage.domain.vote.entity.Vote;
import java.time.LocalDateTime;
import java.util.List;

public record VoteDetailResponse(
    Long id,
    String title,
    String description,
    LocalDateTime startAt,
    LocalDateTime endAt,
    List<VoteAgendaResponse> agendas
) {

  public static VoteDetailResponse from(Vote vote, List<VoteAgendaResponse> agendas) {
    return new VoteDetailResponse(
        vote.getId(),
        vote.getTitle(),
        vote.getDescription(),
        vote.getStartAt(),
        vote.getEndAt(),
        agendas
    );
  }
}
