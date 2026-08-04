package com.keeper.homepage.domain.vote.application;

import static com.keeper.homepage.domain.vote.dto.response.VoteParticipationStatus.NOT_PERMITTED;
import static com.keeper.homepage.domain.vote.dto.response.VoteParticipationStatus.OUTSIDE_VOTING_PERIOD;
import static com.keeper.homepage.domain.vote.dto.response.VoteParticipationStatus.PERMITTED;
import static com.keeper.homepage.domain.vote.dto.response.VoteParticipationStatus.SUBMITTED;
import static com.keeper.homepage.global.error.ErrorCode.VOTE_INACCESSIBLE;
import static com.keeper.homepage.global.error.ErrorCode.VOTE_NOT_FOUND;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.vote.dao.VoteAgendaRepository;
import com.keeper.homepage.domain.vote.dao.VoteOptionRepository;
import com.keeper.homepage.domain.vote.dao.VoteParticipationRepository;
import com.keeper.homepage.domain.vote.dao.VoteRepository;
import com.keeper.homepage.domain.vote.dto.response.VoteAgendaResponse;
import com.keeper.homepage.domain.vote.dto.response.VoteDetailResponse;
import com.keeper.homepage.domain.vote.dto.response.VoteListItemResponse;
import com.keeper.homepage.domain.vote.dto.response.VoteListResponse;
import com.keeper.homepage.domain.vote.dto.response.VoteOptionResponse;
import com.keeper.homepage.domain.vote.dto.response.VoteParticipationStatus;
import com.keeper.homepage.domain.vote.entity.Vote;
import com.keeper.homepage.domain.vote.entity.VoteAgenda;
import com.keeper.homepage.domain.vote.entity.VoteOption;
import com.keeper.homepage.global.error.BusinessException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  private final VoteAgendaRepository voteAgendaRepository;
  private final VoteOptionRepository voteOptionRepository;

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
    LocalDateTime now = LocalDateTime.now();
    List<VoteListItemResponse> responses = votes.stream()
        .map(vote -> VoteListItemResponse.from(
            vote, getParticipationStatus(
                vote, memberId, participatedVoteIds, now)))
        .toList();

    return new VoteListResponse(responses);
  }

  public VoteDetailResponse getVote(Member member, long voteId) {
    Vote vote = voteRepository.findById(voteId)
        .orElseThrow(() -> new BusinessException(voteId, "voteId", VOTE_NOT_FOUND));
    long memberId = member.getId();
    if (!vote.getPermitByMember().contains(memberId)) {
      throw new BusinessException(voteId, "voteId", VOTE_INACCESSIBLE);
    }

    List<VoteAgenda> agendas = voteAgendaRepository.findAllByVoteOrderByDisplayOrderAsc(vote);
    List<Long> agendaIds = agendas.stream()
        .map(VoteAgenda::getId)
        .toList();
    List<VoteOption> options = agendaIds.isEmpty()
        ? List.of()
        : voteOptionRepository.findAllByAgendaIdInOrderByDisplayOrderAsc(agendaIds);
    Map<Long, List<VoteOptionResponse>> optionsByAgendaId = groupOptionsByAgendaId(options);
    List<VoteAgendaResponse> agendaResponses = agendas.stream()
        .map(agenda -> VoteAgendaResponse.from(
            agenda, optionsByAgendaId.getOrDefault(agenda.getId(), List.of())))
        .toList();

    return VoteDetailResponse.from(vote, agendaResponses);
  }

  private static VoteParticipationStatus getParticipationStatus(
      Vote vote,
      long memberId,
      Set<Long> participatedVoteIds,
      LocalDateTime now
  ) {
    if (now.isBefore(vote.getStartAt()) || !now.isBefore(vote.getEndAt())) {
      return OUTSIDE_VOTING_PERIOD;
    }
    if (participatedVoteIds.contains(vote.getId())) {
      return SUBMITTED;
    }

    if (!vote.getPermitByMember().contains(memberId)) {
      return NOT_PERMITTED;
    }
    return PERMITTED;
  }

  private static Map<Long, List<VoteOptionResponse>> groupOptionsByAgendaId(
      List<VoteOption> options
  ) {
    Map<Long, List<VoteOptionResponse>> optionsByAgendaId = new HashMap<>();
    for (VoteOption option : options) {
      optionsByAgendaId.computeIfAbsent(option.getAgenda().getId(), key -> new ArrayList<>())
          .add(VoteOptionResponse.from(option));
    }
    return optionsByAgendaId;
  }
}
