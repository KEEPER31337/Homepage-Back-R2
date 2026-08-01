package com.keeper.homepage.domain.vote.application;

import static com.keeper.homepage.domain.vote.dto.response.VoteParticipationStatus.NOT_PERMITTED;
import static com.keeper.homepage.domain.vote.dto.response.VoteParticipationStatus.OUTSIDE_VOTING_PERIOD;
import static com.keeper.homepage.domain.vote.dto.response.VoteParticipationStatus.PERMITTED;
import static com.keeper.homepage.domain.vote.dto.response.VoteParticipationStatus.SUBMITTED;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.vote.dao.VoteParticipationRepository;
import com.keeper.homepage.domain.vote.dao.VoteRepository;
import com.keeper.homepage.domain.vote.dto.response.VoteListItemResponse;
import com.keeper.homepage.domain.vote.dto.response.VoteListResponse;
import com.keeper.homepage.domain.vote.dto.response.VoteParticipationStatus;
import com.keeper.homepage.domain.vote.entity.Vote;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteService {

  private final VoteRepository voteRepository;
  private final VoteParticipationRepository voteParticipationRepository;

  public VoteListResponse getVotes(Member member, int year) {
    LocalDateTime startAt = LocalDateTime.of(year, 1, 1, 0, 0);
    LocalDateTime endAt = LocalDateTime.of(year + 1, 1, 1, 0, 0);
    List<Vote> votes = voteRepository
        .findAllByStartAtGreaterThanEqualAndStartAtLessThanOrderByStartAtDescIdDesc(
            startAt, endAt);
    if (votes.isEmpty()) {
      return new VoteListResponse(List.of());
    }

    long memberId = member.getId();
    List<Long> voteIds = votes.stream()
        .map(Vote::getId)
        .toList();
    Set<Long> participatedVoteIds = voteParticipationRepository.findParticipatedVoteIds(
        memberId, voteIds);
    Set<String> memberRoles = Set.copyOf(member.getJobs());
    LocalDateTime now = LocalDateTime.now();
    List<VoteListItemResponse> responses = votes.stream()
        .map(vote -> VoteListItemResponse.from(
            vote, getParticipationStatus(
                vote, memberId, memberRoles, participatedVoteIds, now)))
        .toList();

    return new VoteListResponse(responses);
  }

  private static VoteParticipationStatus getParticipationStatus(
      Vote vote,
      long memberId,
      Set<String> memberRoles,
      Set<Long> participatedVoteIds,
      LocalDateTime now
  ) {
    if (now.isBefore(vote.getStartAt()) || !now.isBefore(vote.getEndAt())) {
      return OUTSIDE_VOTING_PERIOD;
    }
    if (participatedVoteIds.contains(vote.getId())) {
      return SUBMITTED;
    }

    boolean permittedByMember = vote.getPermitByMember().contains(memberId);
    boolean permittedByRole = vote.getPermitByRole().stream().anyMatch(memberRoles::contains);
    if (!permittedByMember && !permittedByRole) {
      return NOT_PERMITTED;
    }
    return PERMITTED;
  }
}
