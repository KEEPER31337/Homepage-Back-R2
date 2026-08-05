package com.keeper.homepage.domain.vote.application;

import static com.keeper.homepage.global.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.VOTE_NOT_FOUND;

import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.vote.dao.VoteAgendaRepository;
import com.keeper.homepage.domain.vote.dao.VoteOptionRepository;
import com.keeper.homepage.domain.vote.dao.VoteParticipationRepository;
import com.keeper.homepage.domain.vote.dao.VoteParticipationRepository.VoteParticipationCount;
import com.keeper.homepage.domain.vote.dao.VoteRepository;
import com.keeper.homepage.domain.vote.dto.request.VoteAgendaCreateRequest;
import com.keeper.homepage.domain.vote.dto.request.VoteCreateRequest;
import com.keeper.homepage.domain.vote.dto.request.VoteOptionCreateRequest;
import com.keeper.homepage.domain.vote.dto.response.AdminVoteListItemResponse;
import com.keeper.homepage.domain.vote.dto.response.AdminVoteListResponse;
import com.keeper.homepage.domain.vote.entity.Vote;
import com.keeper.homepage.domain.vote.entity.VoteAgenda;
import com.keeper.homepage.domain.vote.entity.VoteOption;
import com.keeper.homepage.global.error.BusinessException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminVoteService {

  private final VoteRepository voteRepository;
  private final VoteAgendaRepository voteAgendaRepository;
  private final VoteOptionRepository voteOptionRepository;
  private final VoteParticipationRepository voteParticipationRepository;
  private final MemberRepository memberRepository;

  @Transactional(readOnly = true)
  public AdminVoteListResponse getVotes(int year) {
    LocalDateTime startAt = LocalDateTime.of(year, 1, 1, 0, 0);
    LocalDateTime endAt = LocalDateTime.of(year + 1, 1, 1, 0, 0);
    List<Vote> votes = voteRepository
        .findAllByStartAtGreaterThanEqualAndStartAtLessThanOrderByStartAtDescIdDesc(
            startAt, endAt);
    if (votes.isEmpty()) {
      return new AdminVoteListResponse(List.of());
    }

    List<Long> voteIds = votes.stream()
        .map(Vote::getId)
        .toList();
    List<VoteParticipationCount> participantCounts =
        voteParticipationRepository.countParticipantsByVoteIds(voteIds);
    Map<Long, Long> participantCountsByVoteId = new HashMap<>();
    for (VoteParticipationCount count : participantCounts) {
      participantCountsByVoteId.put(count.getVoteId(), count.getParticipantCount());
    }

    List<AdminVoteListItemResponse> responses = votes.stream()
        .map(vote -> AdminVoteListItemResponse.from(
            vote, participantCountsByVoteId.getOrDefault(vote.getId(), 0L)))
        .toList();
    return new AdminVoteListResponse(responses);
  }

  @Transactional
  public long createVote(Member creator, VoteCreateRequest request) {
    validatePermitMembers(request.permitByUserIds());

    Vote vote = voteRepository.save(createVoteEntity(creator, request));
    List<VoteAgenda> agendas = createAgendaEntities(vote, request.agendas());
    voteAgendaRepository.saveAll(agendas);
    List<VoteOption> options = createOptionEntities(agendas, request.agendas());
    voteOptionRepository.saveAll(options);

    return vote.getId();
  }

  @Transactional
  public void deleteVote(long voteId) {
    Vote vote = voteRepository.findById(voteId)
        .orElseThrow(() -> new BusinessException(voteId, "voteId", VOTE_NOT_FOUND));
    voteRepository.delete(vote);
  }

  private void validatePermitMembers(List<Long> permitByUserIds) {
    if (permitByUserIds.isEmpty()) {
      return;
    }

    Set<Long> requestedMemberIds = new LinkedHashSet<>(permitByUserIds);
    Set<Long> existingMemberIds = new LinkedHashSet<>();
    memberRepository.findAllById(requestedMemberIds)
        .forEach(member -> existingMemberIds.add(member.getId()));

    requestedMemberIds.removeAll(existingMemberIds);
    if (!requestedMemberIds.isEmpty()) {
      throw new BusinessException(requestedMemberIds, "permitByUserIds", MEMBER_NOT_FOUND);
    }
  }

  private Vote createVoteEntity(Member creator, VoteCreateRequest request) {
    return Vote.builder()
        .title(request.title())
        .description(request.description())
        .permitByMember(request.permitByUserIds())
        .startAt(request.startAt())
        .endAt(request.endAt())
        .createdBy(creator)
        .build();
  }

  private List<VoteAgenda> createAgendaEntities(Vote vote,
      List<VoteAgendaCreateRequest> requests) {
    List<VoteAgenda> agendas = new ArrayList<>(requests.size());
    for (int index = 0; index < requests.size(); index++) {
      VoteAgendaCreateRequest request = requests.get(index);
      agendas.add(VoteAgenda.builder()
          .vote(vote)
          .title(request.title())
          .displayOrder(index)
          .minSelect(request.minSelect())
          .maxSelect(request.maxSelect())
          .build());
    }
    return agendas;
  }

  private List<VoteOption> createOptionEntities(List<VoteAgenda> agendas,
      List<VoteAgendaCreateRequest> agendaRequests) {
    List<VoteOption> options = new ArrayList<>();
    for (int agendaIndex = 0; agendaIndex < agendas.size(); agendaIndex++) {
      VoteAgenda agenda = agendas.get(agendaIndex);
      List<VoteOptionCreateRequest> optionRequests = agendaRequests.get(agendaIndex).options();
      for (int optionIndex = 0; optionIndex < optionRequests.size(); optionIndex++) {
        options.add(VoteOption.builder()
            .agenda(agenda)
            .content(optionRequests.get(optionIndex).content())
            .displayOrder(optionIndex)
            .build());
      }
    }
    return options;
  }
}
