package com.keeper.homepage.domain.vote.application;

import static com.keeper.homepage.domain.vote.application.VotePermissionChecker.isPermitted;
import static com.keeper.homepage.global.error.ErrorCode.VOTE_AGENDA_MISMATCH;
import static com.keeper.homepage.global.error.ErrorCode.VOTE_ALREADY_PARTICIPATED;
import static com.keeper.homepage.global.error.ErrorCode.VOTE_INACCESSIBLE;
import static com.keeper.homepage.global.error.ErrorCode.VOTE_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.VOTE_NOT_IN_PROGRESS;
import static com.keeper.homepage.global.error.ErrorCode.VOTE_OPTION_MISMATCH;
import static com.keeper.homepage.global.error.ErrorCode.VOTE_SELECTION_COUNT_INVALID;
import static com.keeper.homepage.global.error.ErrorCode.VOTE_SELECTION_DUPLICATE;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.vote.dao.VoteAgendaRepository;
import com.keeper.homepage.domain.vote.dao.VoteChoiceRepository;
import com.keeper.homepage.domain.vote.dao.VoteOptionRepository;
import com.keeper.homepage.domain.vote.dao.VoteParticipationRepository;
import com.keeper.homepage.domain.vote.dao.VoteReceiptRepository;
import com.keeper.homepage.domain.vote.dao.VoteRepository;
import com.keeper.homepage.domain.vote.dto.request.VoteParticipationRequest;
import com.keeper.homepage.domain.vote.dto.request.VoteSelectionRequest;
import com.keeper.homepage.domain.vote.dto.response.VoteParticipationResponse;
import com.keeper.homepage.domain.vote.entity.Vote;
import com.keeper.homepage.domain.vote.entity.VoteAgenda;
import com.keeper.homepage.domain.vote.entity.VoteChoice;
import com.keeper.homepage.domain.vote.entity.VoteOption;
import com.keeper.homepage.domain.vote.entity.VoteParticipation;
import com.keeper.homepage.domain.vote.entity.VoteReceipt;
import com.keeper.homepage.global.error.BusinessException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteParticipationService {

  private final VoteRepository voteRepository;
  private final VoteAgendaRepository voteAgendaRepository;
  private final VoteOptionRepository voteOptionRepository;
  private final VoteParticipationRepository voteParticipationRepository;
  private final VoteReceiptRepository voteReceiptRepository;
  private final VoteChoiceRepository voteChoiceRepository;

  @Transactional
  public VoteParticipationResponse participate(
      Member member,
      long voteId,
      VoteParticipationRequest request
  ) {
    Vote vote = voteRepository.findById(voteId)
        .orElseThrow(() -> new BusinessException(voteId, "voteId", VOTE_NOT_FOUND));
    validateParticipationPermission(member, vote);
    validateNotParticipated(member, vote);
    List<VoteOption> selectedOptions = validateSelections(vote, request.selections());

    saveParticipation(member, vote);
    VoteReceipt receipt = saveReceipt(vote);
    saveChoices(receipt, selectedOptions);
    return VoteParticipationResponse.from(receipt, selectedOptions);
  }

  private static void validateParticipationPermission(Member member, Vote vote) {
    LocalDateTime now = LocalDateTime.now();
    if (now.isBefore(vote.getStartAt()) || !now.isBefore(vote.getEndAt())) {
      throw new BusinessException(vote.getId(), "voteId", VOTE_NOT_IN_PROGRESS);
    }

    Set<String> memberRoles = Set.copyOf(member.getJobs());
    if (!isPermitted(vote, member.getId(), memberRoles)) {
      throw new BusinessException(vote.getId(), "voteId", VOTE_INACCESSIBLE);
    }
  }

  private void validateNotParticipated(Member member, Vote vote) {
    if (voteParticipationRepository.existsByVoteAndMember(vote, member)) {
      throw new BusinessException(vote.getId(), "voteId", VOTE_ALREADY_PARTICIPATED);
    }
  }

  private List<VoteOption> validateSelections(Vote vote, List<VoteSelectionRequest> selections) {
    List<VoteAgenda> agendas = voteAgendaRepository.findAllByVoteOrderByDisplayOrderAsc(vote);
    Map<Long, VoteAgenda> agendasById = new HashMap<>();
    for (VoteAgenda agenda : agendas) {
      agendasById.put(agenda.getId(), agenda);
    }

    Set<Long> requestedAgendaIds = new HashSet<>();
    for (VoteSelectionRequest selection : selections) {
      if (!requestedAgendaIds.add(selection.agendaId())) {
        throw new BusinessException(
            selection.agendaId(), "selections.agendaId", VOTE_SELECTION_DUPLICATE);
      }
    }
    if (!requestedAgendaIds.equals(agendasById.keySet())) {
      throw new BusinessException(
          requestedAgendaIds, "selections.agendaId", VOTE_AGENDA_MISMATCH);
    }

    Set<Long> requestedOptionIds = new HashSet<>();
    for (VoteSelectionRequest selection : selections) {
      VoteAgenda agenda = agendasById.get(selection.agendaId());
      int selectedCount = selection.optionIds().size();
      if (selectedCount < agenda.getMinSelect() || selectedCount > agenda.getMaxSelect()) {
        throw new BusinessException(
            selection.optionIds(), "selections.optionIds", VOTE_SELECTION_COUNT_INVALID);
      }
      for (Long optionId : selection.optionIds()) {
        if (!requestedOptionIds.add(optionId)) {
          throw new BusinessException(
              optionId, "selections.optionIds", VOTE_SELECTION_DUPLICATE);
        }
      }
    }

    List<VoteOption> options = voteOptionRepository.findAllWithAgendaByIdIn(requestedOptionIds);
    if (options.size() != requestedOptionIds.size()) {
      throw new BusinessException(
          requestedOptionIds, "selections.optionIds", VOTE_OPTION_MISMATCH);
    }
    Map<Long, VoteOption> optionsById = new HashMap<>();
    for (VoteOption option : options) {
      optionsById.put(option.getId(), option);
    }

    List<VoteOption> selectedOptions = new ArrayList<>(requestedOptionIds.size());
    for (VoteSelectionRequest selection : selections) {
      for (Long optionId : selection.optionIds()) {
        VoteOption option = optionsById.get(optionId);
        if (!option.getAgenda().getId().equals(selection.agendaId())) {
          throw new BusinessException(
              optionId, "selections.optionIds", VOTE_OPTION_MISMATCH);
        }
        selectedOptions.add(option);
      }
    }
    return selectedOptions;
  }

  private void saveParticipation(Member member, Vote vote) {
    VoteParticipation participation = VoteParticipation.builder()
        .vote(vote)
        .member(member)
        .voterNameSnapshot(member.getRealName())
        .voterGenerationSnapshot(Float.valueOf(member.getGeneration()))
        .build();
    try {
      voteParticipationRepository.saveAndFlush(participation);
    } catch (DataIntegrityViolationException exception) {
      throw new BusinessException(vote.getId(), "voteId", VOTE_ALREADY_PARTICIPATED);
    }
  }

  private VoteReceipt saveReceipt(Vote vote) {
    VoteReceipt receipt = VoteReceipt.builder()
        .vote(vote)
        .build();
    return voteReceiptRepository.saveAndFlush(receipt);
  }

  private void saveChoices(VoteReceipt receipt, List<VoteOption> selectedOptions) {
    List<VoteChoice> choices = selectedOptions.stream()
        .map(option -> VoteChoice.builder()
            .receipt(receipt)
            .option(option)
            .build())
        .toList();
    voteChoiceRepository.saveAll(choices);
  }
}
