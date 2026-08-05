package com.keeper.homepage.domain.vote.dto.response;

import com.keeper.homepage.domain.vote.entity.Vote;
import java.time.LocalDateTime;
import java.util.List;

public record AdminVoteListItemResponse(
    Long id,
    String title,
    String description,
    LocalDateTime startAt,
    LocalDateTime endAt,
    List<Long> permitByUserIds,
    long participantCount
) {

  public static AdminVoteListItemResponse from(Vote vote, long participantCount) {
    return new AdminVoteListItemResponse(
        vote.getId(),
        vote.getTitle(),
        vote.getDescription(),
        vote.getStartAt(),
        vote.getEndAt(),
        List.copyOf(vote.getPermitByMember()),
        participantCount
    );
  }
}
