package com.keeper.homepage.domain.vote.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class VoteParticipationServiceTest {

  @Mock
  private VoteRepository voteRepository;

  @Mock
  private VoteAgendaRepository voteAgendaRepository;

  @Mock
  private VoteOptionRepository voteOptionRepository;

  @Mock
  private VoteParticipationRepository voteParticipationRepository;

  @Mock
  private VoteReceiptRepository voteReceiptRepository;

  @Mock
  private VoteChoiceRepository voteChoiceRepository;

  @InjectMocks
  private VoteParticipationService voteParticipationService;

  @Test
  void participateSavesParticipationReceiptAndChoices() {
    Member member = permittedMember();
    when(member.getRealName()).thenReturn("홍길동");
    when(member.getGeneration()).thenReturn("17.5");
    Vote vote = openVote();
    VoteAgenda president = agenda(20L, vote, "회장", 0, 1, 1);
    VoteAgenda vicePresident = agenda(21L, vote, "부회장", 1, 1, 2);
    VoteOption option101 = option(101L, president, "후보 A", 0);
    VoteOption option201 = option(201L, vicePresident, "후보 B", 0);
    VoteOption option202 = option(202L, vicePresident, "후보 C", 1);
    VoteParticipationRequest request = request(
        new VoteSelectionRequest(21L, List.of(202L, 201L)),
        new VoteSelectionRequest(20L, List.of(101L)));
    LocalDateTime createdAt = LocalDateTime.of(2026, 8, 3, 12, 0);
    when(voteRepository.findById(42L)).thenReturn(Optional.of(vote));
    when(voteAgendaRepository.findAllByVoteOrderByDisplayOrderAsc(vote))
        .thenReturn(List.of(president, vicePresident));
    when(voteOptionRepository.findAllWithAgendaByIdIn(any()))
        .thenReturn(List.of(option202, option101, option201));
    when(voteReceiptRepository.saveAndFlush(any(VoteReceipt.class)))
        .thenAnswer(invocation -> {
          VoteReceipt receipt = invocation.getArgument(0);
          ReflectionTestUtils.setField(receipt, "createdAt", createdAt);
          return receipt;
        });

    VoteParticipationResponse response =
        voteParticipationService.participate(member, 42L, request);

    ArgumentCaptor<VoteParticipation> participationCaptor =
        ArgumentCaptor.forClass(VoteParticipation.class);
    verify(voteParticipationRepository).saveAndFlush(participationCaptor.capture());
    VoteParticipation participation = participationCaptor.getValue();
    assertThat(participation.getVote()).isSameAs(vote);
    assertThat(participation.getMember()).isSameAs(member);
    assertThat(participation.getVoterNameSnapshot()).isEqualTo("홍길동");
    assertThat(participation.getVoterGenerationSnapshot()).isEqualTo(17.5F);
    assertThat(participation.getId()).isNotNull();

    ArgumentCaptor<VoteReceipt> receiptCaptor = ArgumentCaptor.forClass(VoteReceipt.class);
    verify(voteReceiptRepository).saveAndFlush(receiptCaptor.capture());
    VoteReceipt receipt = receiptCaptor.getValue();
    assertThat(receipt.getToken()).isNotNull();
    assertThat(receipt.getToken().version()).isEqualTo(4);
    assertThat(receipt.getToken().variant()).isEqualTo(2);
    assertThat(receipt.getCreatedAt()).isEqualTo(createdAt);
    assertThat(receipt.getVote()).isSameAs(vote);

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<VoteChoice>> choicesCaptor = ArgumentCaptor.forClass(List.class);
    verify(voteChoiceRepository).saveAll(choicesCaptor.capture());
    assertThat(choicesCaptor.getValue())
        .extracting(choice -> choice.getOption().getId())
        .containsExactly(202L, 201L, 101L);
    assertThat(choicesCaptor.getValue())
        .allMatch(choice -> choice.getReceipt() == receipt);

    InOrder saveOrder = inOrder(
        voteParticipationRepository, voteReceiptRepository, voteChoiceRepository);
    saveOrder.verify(voteParticipationRepository).saveAndFlush(participation);
    saveOrder.verify(voteReceiptRepository).saveAndFlush(receipt);
    saveOrder.verify(voteChoiceRepository).saveAll(choicesCaptor.getValue());

    assertThat(response.receiptToken()).isEqualTo(receipt.getToken());
    assertThat(response.selections())
        .extracting(selection -> selection.agendaId())
        .containsExactly(20L, 21L);
    assertThat(response.selections().get(0).agendaTitle()).isEqualTo("회장");
    assertThat(response.selections().get(0).options())
        .extracting(
            optionResponse -> optionResponse.optionId(),
            optionResponse -> optionResponse.content())
        .containsExactly(org.assertj.core.groups.Tuple.tuple(101L, "후보 A"));
    assertThat(response.selections().get(1).agendaTitle()).isEqualTo("부회장");
    assertThat(response.selections().get(1).options())
        .extracting(
            optionResponse -> optionResponse.optionId(),
            optionResponse -> optionResponse.content())
        .containsExactly(
            org.assertj.core.groups.Tuple.tuple(201L, "후보 B"),
            org.assertj.core.groups.Tuple.tuple(202L, "후보 C"));
  }

  @Test
  void participateRejectsVoteOutsideVotingPeriodBeforePermissionCheck() {
    Member member = mock(Member.class);
    Vote vote = vote(
        LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
        List.of("ROLE_회원"));
    when(voteRepository.findById(42L)).thenReturn(Optional.of(vote));

    assertBusinessException(
        () -> voteParticipationService.participate(member, 42L, singleSelectionRequest()),
        HttpStatus.CONFLICT,
        "투표 가능한 기간이 아닙니다.");

    verify(member, never()).getJobs();
    verifyNoInteractions(
        voteAgendaRepository,
        voteOptionRepository,
        voteParticipationRepository,
        voteReceiptRepository,
        voteChoiceRepository);
  }

  @Test
  void participateRejectsMemberWithoutPermission() {
    Member member = mock(Member.class);
    when(member.getId()).thenReturn(10L);
    when(member.getJobs()).thenReturn(List.of("ROLE_회원"));
    Vote vote = vote(
        LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
        List.of("ROLE_회장"));
    when(voteRepository.findById(42L)).thenReturn(Optional.of(vote));

    assertBusinessException(
        () -> voteParticipationService.participate(member, 42L, singleSelectionRequest()),
        HttpStatus.FORBIDDEN,
        "투표에 참여할 권한이 없습니다.");

    verifyNoInteractions(
        voteAgendaRepository,
        voteOptionRepository,
        voteParticipationRepository,
        voteReceiptRepository,
        voteChoiceRepository);
  }

  @Test
  void participateRejectsMemberWhoAlreadyParticipated() {
    Member member = permittedMember();
    Vote vote = openVote();
    when(voteRepository.findById(42L)).thenReturn(Optional.of(vote));
    when(voteParticipationRepository.existsByVoteAndMember(vote, member)).thenReturn(true);

    assertBusinessException(
        () -> voteParticipationService.participate(member, 42L, singleSelectionRequest()),
        HttpStatus.CONFLICT,
        "이미 참여한 투표입니다.");

    verifyNoInteractions(voteAgendaRepository, voteOptionRepository, voteChoiceRepository);
    verify(voteReceiptRepository, never()).saveAndFlush(any());
  }

  @Test
  void participateRejectsMissingAgenda() {
    Member member = permittedMember();
    Vote vote = openVote();
    VoteAgenda president = agenda(20L, vote, "회장", 0, 1, 1);
    VoteAgenda vicePresident = agenda(21L, vote, "부회장", 1, 1, 1);
    when(voteRepository.findById(42L)).thenReturn(Optional.of(vote));
    when(voteAgendaRepository.findAllByVoteOrderByDisplayOrderAsc(vote))
        .thenReturn(List.of(president, vicePresident));

    assertBusinessException(
        () -> voteParticipationService.participate(member, 42L, singleSelectionRequest()),
        HttpStatus.BAD_REQUEST,
        "투표에 속하지 않거나 누락된 안건이 있습니다.");

    verifyNoInteractions(voteOptionRepository, voteChoiceRepository);
    verify(voteParticipationRepository, never()).saveAndFlush(any());
    verify(voteReceiptRepository, never()).saveAndFlush(any());
  }

  @Test
  void participateRejectsDuplicateAgenda() {
    Member member = permittedMember();
    Vote vote = openVote();
    VoteAgenda president = agenda(20L, vote, "회장", 0, 1, 2);
    when(voteRepository.findById(42L)).thenReturn(Optional.of(vote));
    when(voteAgendaRepository.findAllByVoteOrderByDisplayOrderAsc(vote))
        .thenReturn(List.of(president));
    VoteParticipationRequest duplicateAgendaRequest = request(
        new VoteSelectionRequest(20L, List.of(101L)),
        new VoteSelectionRequest(20L, List.of(102L)));

    assertBusinessException(
        () -> voteParticipationService.participate(member, 42L, duplicateAgendaRequest),
        HttpStatus.BAD_REQUEST,
        "중복된 안건 또는 선택지가 있습니다.");

    verifyNoInteractions(voteOptionRepository, voteChoiceRepository);
  }

  @Test
  void participateRejectsDuplicateOption() {
    Member member = permittedMember();
    Vote vote = openVote();
    VoteAgenda president = agenda(20L, vote, "회장", 0, 1, 2);
    when(voteRepository.findById(42L)).thenReturn(Optional.of(vote));
    when(voteAgendaRepository.findAllByVoteOrderByDisplayOrderAsc(vote))
        .thenReturn(List.of(president));
    VoteParticipationRequest duplicateOptionRequest = request(
        new VoteSelectionRequest(20L, List.of(101L, 101L)));

    assertBusinessException(
        () -> voteParticipationService.participate(member, 42L, duplicateOptionRequest),
        HttpStatus.BAD_REQUEST,
        "중복된 안건 또는 선택지가 있습니다.");

    verifyNoInteractions(voteOptionRepository, voteChoiceRepository);
  }

  @Test
  void participateRejectsSelectionCountOutsideAgendaRange() {
    Member member = permittedMember();
    Vote vote = openVote();
    VoteAgenda president = agenda(20L, vote, "회장", 0, 2, 2);
    when(voteRepository.findById(42L)).thenReturn(Optional.of(vote));
    when(voteAgendaRepository.findAllByVoteOrderByDisplayOrderAsc(vote))
        .thenReturn(List.of(president));

    assertBusinessException(
        () -> voteParticipationService.participate(member, 42L, singleSelectionRequest()),
        HttpStatus.BAD_REQUEST,
        "안건의 선택 가능 개수를 벗어났습니다.");

    verifyNoInteractions(voteOptionRepository, voteChoiceRepository);
  }

  @Test
  void participateRejectsOptionBelongingToAnotherAgenda() {
    Member member = permittedMember();
    Vote vote = openVote();
    VoteAgenda president = agenda(20L, vote, "회장", 0, 1, 1);
    VoteAgenda vicePresident = agenda(21L, vote, "부회장", 1, 1, 1);
    VoteOption presidentOption = option(101L, president, "후보 A", 0);
    VoteOption vicePresidentOption = option(201L, vicePresident, "후보 B", 0);
    VoteParticipationRequest request = request(
        new VoteSelectionRequest(20L, List.of(201L)),
        new VoteSelectionRequest(21L, List.of(101L)));
    when(voteRepository.findById(42L)).thenReturn(Optional.of(vote));
    when(voteAgendaRepository.findAllByVoteOrderByDisplayOrderAsc(vote))
        .thenReturn(List.of(president, vicePresident));
    when(voteOptionRepository.findAllWithAgendaByIdIn(any()))
        .thenReturn(List.of(presidentOption, vicePresidentOption));

    assertBusinessException(
        () -> voteParticipationService.participate(member, 42L, request),
        HttpStatus.BAD_REQUEST,
        "안건에 속하지 않거나 존재하지 않는 선택지가 있습니다.");

    verifyNoInteractions(voteChoiceRepository);
  }

  @Test
  void participateRejectsUnknownOption() {
    Member member = permittedMember();
    Vote vote = openVote();
    VoteAgenda president = agenda(20L, vote, "회장", 0, 1, 1);
    when(voteRepository.findById(42L)).thenReturn(Optional.of(vote));
    when(voteAgendaRepository.findAllByVoteOrderByDisplayOrderAsc(vote))
        .thenReturn(List.of(president));
    when(voteOptionRepository.findAllWithAgendaByIdIn(any())).thenReturn(List.of());

    assertBusinessException(
        () -> voteParticipationService.participate(member, 42L, singleSelectionRequest()),
        HttpStatus.BAD_REQUEST,
        "안건에 속하지 않거나 존재하지 않는 선택지가 있습니다.");

    verifyNoInteractions(voteChoiceRepository);
  }

  @Test
  void participateMapsDatabaseParticipationConflictToConflictResponse() {
    Member member = permittedMember();
    when(member.getRealName()).thenReturn("홍길동");
    when(member.getGeneration()).thenReturn("17.5");
    Vote vote = openVote();
    VoteAgenda president = agenda(20L, vote, "회장", 0, 1, 1);
    VoteOption option = option(101L, president, "후보 A", 0);
    when(voteRepository.findById(42L)).thenReturn(Optional.of(vote));
    when(voteAgendaRepository.findAllByVoteOrderByDisplayOrderAsc(vote))
        .thenReturn(List.of(president));
    when(voteOptionRepository.findAllWithAgendaByIdIn(any()))
        .thenReturn(List.of(option));
    when(voteParticipationRepository.saveAndFlush(any()))
        .thenThrow(new DataIntegrityViolationException("duplicate"));

    assertBusinessException(
        () -> voteParticipationService.participate(member, 42L, singleSelectionRequest()),
        HttpStatus.CONFLICT,
        "이미 참여한 투표입니다.");

    verify(voteReceiptRepository, never()).saveAndFlush(any());
    verifyNoInteractions(voteChoiceRepository);
  }

  private Member permittedMember() {
    Member member = mock(Member.class);
    when(member.getId()).thenReturn(10L);
    when(member.getJobs()).thenReturn(List.of("ROLE_회원"));
    return member;
  }

  private static Vote openVote() {
    return vote(
        LocalDateTime.now().minusDays(1),
        LocalDateTime.now().plusDays(1),
        List.of("ROLE_회원"));
  }

  private static Vote vote(
      LocalDateTime startAt,
      LocalDateTime endAt,
      List<String> permitByRole
  ) {
    Vote vote = Vote.builder()
        .title("회장 선거")
        .description("설명")
        .permitByRole(permitByRole)
        .permitByMember(List.of())
        .startAt(startAt)
        .endAt(endAt)
        .build();
    ReflectionTestUtils.setField(vote, "id", 42L);
    return vote;
  }

  private static VoteAgenda agenda(
      long id,
      Vote vote,
      String title,
      int displayOrder,
      int minSelect,
      int maxSelect
  ) {
    VoteAgenda agenda = VoteAgenda.builder()
        .vote(vote)
        .title(title)
        .displayOrder(displayOrder)
        .minSelect(minSelect)
        .maxSelect(maxSelect)
        .build();
    ReflectionTestUtils.setField(agenda, "id", id);
    return agenda;
  }

  private static VoteOption option(
      long id,
      VoteAgenda agenda,
      String content,
      int displayOrder
  ) {
    VoteOption option = VoteOption.builder()
        .agenda(agenda)
        .content(content)
        .displayOrder(displayOrder)
        .build();
    ReflectionTestUtils.setField(option, "id", id);
    return option;
  }

  private static VoteParticipationRequest singleSelectionRequest() {
    return request(new VoteSelectionRequest(20L, List.of(101L)));
  }

  private static VoteParticipationRequest request(VoteSelectionRequest... selections) {
    return new VoteParticipationRequest(List.of(selections));
  }

  private static void assertBusinessException(
      org.assertj.core.api.ThrowableAssert.ThrowingCallable callable,
      HttpStatus status,
      String message
  ) {
    assertThatThrownBy(callable)
        .isInstanceOfSatisfying(BusinessException.class, exception -> {
          assertThat(exception.getHttpStatus()).isEqualTo(status);
          assertThat(exception.getMessage()).isEqualTo(message);
        });
  }
}
