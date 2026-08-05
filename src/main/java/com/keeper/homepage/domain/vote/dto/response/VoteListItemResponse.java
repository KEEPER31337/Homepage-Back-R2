package com.keeper.homepage.domain.vote.dto.response;

import com.keeper.homepage.domain.vote.entity.Vote;
import java.time.LocalDateTime;

public record VoteListItemResponse(
    Long id,
    String title,
    String description,
    LocalDateTime startAt,
    LocalDateTime endAt,
    int participated
) {

  public static VoteListItemResponse from(Vote vote, VoteParticipationStatus status) {
    return new VoteListItemResponse(
        vote.getId(),
        vote.getTitle(),
        vote.getDescription(),
        vote.getStartAt(),
        vote.getEndAt(),
        status.code()
    );
  }
}
