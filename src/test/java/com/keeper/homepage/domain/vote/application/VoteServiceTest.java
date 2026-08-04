package com.keeper.homepage.domain.vote.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.vote.dao.VoteAgendaRepository;
import com.keeper.homepage.domain.vote.dao.VoteOptionRepository;
import com.keeper.homepage.domain.vote.dao.VoteParticipationRepository;
import com.keeper.homepage.domain.vote.dao.VoteReceiptRepository;
import com.keeper.homepage.domain.vote.dao.VoteRepository;
import com.keeper.homepage.domain.vote.dto.response.VoteDetailResponse;
import com.keeper.homepage.domain.vote.dto.response.VoteListResponse;
import com.keeper.homepage.domain.vote.dto.response.VoteResultResponse;
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
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

  @Mock
  private VoteRepository voteRepository;

  @Mock
  private VoteParticipationRepository voteParticipationRepository;

  @Mock
  private VoteReceiptRepository voteReceiptRepository;

  @Mock
  private VoteAgendaRepository voteAgendaRepository;

  @Mock
  private VoteOptionRepository voteOptionRepository;

  @InjectMocks
  private VoteService voteService;

  @Test
  void getVotesReturnsParticipationStatusForEachVote() {
    Member member = mock(Member.class);
    when(member.getId()).thenReturn(10L);
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime openStartAt = now.minusDays(1);
    LocalDateTime openEndAt = now.plusDays(1);
    LocalDateTime futureStartAt = now.plusDays(1);
    LocalDateTime futureEndAt = now.plusDays(2);
    Vote firstPermittedVote = vote(
        5L, "첫 번째 허용 투표", List.of(10L), openStartAt, openEndAt);
    Vote secondPermittedVote = vote(
        4L, "두 번째 허용 투표", List.of(10L), openStartAt, openEndAt);
    Vote notPermittedVote = vote(
        3L, "권한 없는 투표", List.of(), openStartAt, openEndAt);
    Vote submittedVote = vote(
        2L, "제출한 투표", List.of(10L), openStartAt, openEndAt);
    Vote outsideVotingPeriodVote = vote(
        1L, "기간 외 투표", List.of(10L), futureStartAt, futureEndAt);
    when(voteRepository
        .findAllByStartAtGreaterThanEqualAndStartAtLessThanOrderByStartAtDescIdDesc(
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2027, 1, 1, 0, 0)))
        .thenReturn(List.of(
            firstPermittedVote,
            secondPermittedVote,
            notPermittedVote,
            submittedVote,
            outsideVotingPeriodVote));
    when(voteParticipationRepository.findParticipatedVoteIds(
        10L, List.of(5L, 4L, 3L, 2L, 1L)))
        .thenReturn(Set.of(2L, 1L));

    VoteListResponse response = voteService.getVotes(member, 2026);

    assertThat(response.votes()).hasSize(5);
    assertThat(response.votes())
        .extracting(item -> item.id())
        .containsExactly(5L, 4L, 3L, 2L, 1L);
    assertThat(response.votes())
        .extracting(item -> item.participated())
        .containsExactly(1, 1, 2, 3, 4);
    verify(voteParticipationRepository)
        .findParticipatedVoteIds(10L, List.of(5L, 4L, 3L, 2L, 1L));
  }

  @Test
  void getVotesDoesNotQueryParticipationWhenVoteListIsEmpty() {
    Member member = mock(Member.class);
    when(voteRepository
        .findAllByStartAtGreaterThanEqualAndStartAtLessThanOrderByStartAtDescIdDesc(
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2027, 1, 1, 0, 0)))
        .thenReturn(List.of());

    VoteListResponse response = voteService.getVotes(member, 2026);

    assertThat(response.votes()).isEmpty();
    verifyNoInteractions(voteParticipationRepository);
  }

  @Test
  void getVoteReturnsAgendasAndOptionsInDisplayOrder() {
    Member member = mock(Member.class);
    when(member.getId()).thenReturn(10L);
    Vote vote = vote(
        42L,
        "회장 선거",
        List.of(10L),
        LocalDateTime.of(2026, 8, 1, 0, 0),
        LocalDateTime.of(2026, 8, 2, 0, 0));
    VoteAgenda firstAgenda = agenda(100L, vote, "회장 선출", 0, 1, 1);
    VoteAgenda secondAgenda = agenda(101L, vote, "부회장 선출", 1, 1, 2);
    VoteOption firstOption = option(1000L, firstAgenda, "후보 A", 0);
    VoteOption secondOption = option(1001L, firstAgenda, "후보 B", 1);
    VoteOption thirdOption = option(1002L, secondAgenda, "후보 C", 0);
    when(voteRepository.findById(42L)).thenReturn(Optional.of(vote));
    when(voteAgendaRepository.findAllByVoteOrderByDisplayOrderAsc(vote))
        .thenReturn(List.of(firstAgenda, secondAgenda));
    when(voteOptionRepository.findAllByAgendaIdInOrderByDisplayOrderAsc(List.of(100L, 101L)))
        .thenReturn(List.of(firstOption, thirdOption, secondOption));

    VoteDetailResponse response = voteService.getVote(member, 42L);

    assertThat(response.id()).isEqualTo(42L);
    assertThat(response.title()).isEqualTo("회장 선거");
    assertThat(response.agendas())
        .extracting(agenda -> agenda.id())
        .containsExactly(100L, 101L);
    assertThat(response.agendas().get(0).options())
        .extracting(option -> option.id())
        .containsExactly(1000L, 1001L);
    assertThat(response.agendas().get(1).options())
        .extracting(option -> option.id())
        .containsExactly(1002L);
  }

  @Test
  void getVoteRejectsMemberWithoutPermission() {
    Member member = mock(Member.class);
    when(member.getId()).thenReturn(10L);
    Vote vote = vote(
        42L,
        "회장 선거",
        List.of(11L),
        LocalDateTime.of(2026, 8, 1, 0, 0),
        LocalDateTime.of(2026, 8, 2, 0, 0));
    when(voteRepository.findById(42L)).thenReturn(Optional.of(vote));

    assertThatThrownBy(() -> voteService.getVote(member, 42L))
        .isInstanceOfSatisfying(BusinessException.class, exception -> {
          assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
          assertThat(exception.getFieldName()).isEqualTo("voteId");
        });

    verifyNoInteractions(voteAgendaRepository, voteOptionRepository);
  }

  @Test
  void getVoteRejectsMissingVote() {
    Member member = mock(Member.class);
    when(voteRepository.findById(42L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> voteService.getVote(member, 42L))
        .isInstanceOfSatisfying(BusinessException.class, exception -> {
          assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
          assertThat(exception.getFieldName()).isEqualTo("voteId");
        });

    verifyNoInteractions(voteAgendaRepository, voteOptionRepository);
  }

  @Test
  void getVoteResultReturnsParticipantsReceiptsAndVoteAfterVoteEnds() {
    Vote vote = vote(
        42L,
        "회장 선거",
        List.of(10L),
        LocalDateTime.now().minusDays(2),
        LocalDateTime.now().minusDays(1));
    VoteAgenda president = agenda(20L, vote, "회장", 0, 1, 1);
    VoteAgenda vicePresident = agenda(21L, vote, "부회장", 1, 1, 2);
    VoteOption option101 = option(101L, president, "후보 A", 0);
    VoteOption option201 = option(201L, vicePresident, "후보 B", 0);
    VoteOption option202 = option(202L, vicePresident, "후보 C", 1);
    VoteParticipation secondParticipant = participation(vote, "홍길동", 17.5F, 1L);
    VoteParticipation firstParticipant = participation(vote, "김철수", 18.0F);
    VoteReceipt secondReceipt = receipt(
        vote,
        "ffffffff-ffff-4fff-bfff-ffffffffffff",
        option202,
        option101,
        option201);
    VoteReceipt firstReceipt = receipt(
        vote,
        "00000000-0000-4000-8000-000000000000",
        option201,
        option101);
    when(voteRepository.findById(42L)).thenReturn(Optional.of(vote));
    when(voteParticipationRepository.findAllByVote(vote))
        .thenReturn(List.of(secondParticipant, firstParticipant));
    when(voteReceiptRepository.findAllWithChoicesByVote(vote))
        .thenReturn(List.of(secondReceipt, firstReceipt));
    when(voteAgendaRepository.findAllByVoteOrderByDisplayOrderAsc(vote))
        .thenReturn(List.of(president, vicePresident));
    when(voteOptionRepository.findAllByAgendaIdInOrderByDisplayOrderAsc(List.of(20L, 21L)))
        .thenReturn(List.of(option101, option201, option202));

    VoteResultResponse response = voteService.getVoteResult(42L);

    assertThat(response.participations())
        .extracting(
            participation -> participation.realName(),
            participation -> participation.generation())
        .containsExactly(
            org.assertj.core.groups.Tuple.tuple("김철수", "18.0"),
            org.assertj.core.groups.Tuple.tuple("홍길동 (홈페이지 탈퇴한 회원)", "17.5"));
    assertThat(response.receiptTokenChoices())
        .extracting(receipt -> receipt.receiptToken().toString())
        .containsExactly(
            "00000000-0000-4000-8000-000000000000",
            "ffffffff-ffff-4fff-bfff-ffffffffffff");
    assertThat(response.receiptTokenChoices().get(0).choices())
        .extracting(choice -> choice.agendaId(), choice -> choice.optionIds())
        .containsExactly(
            org.assertj.core.groups.Tuple.tuple(20L, List.of(101L)),
            org.assertj.core.groups.Tuple.tuple(21L, List.of(201L)));
    assertThat(response.receiptTokenChoices().get(1).choices())
        .extracting(choice -> choice.agendaId(), choice -> choice.optionIds())
        .containsExactly(
            org.assertj.core.groups.Tuple.tuple(20L, List.of(101L)),
            org.assertj.core.groups.Tuple.tuple(21L, List.of(201L, 202L)));
    assertThat(response.vote().id()).isEqualTo(42L);
    assertThat(response.vote().agendas())
        .extracting(agenda -> agenda.id())
        .containsExactly(20L, 21L);
  }

  @Test
  void getVoteResultRejectsRequestBeforeVoteEnds() {
    Vote vote = vote(
        42L,
        "회장 선거",
        List.of(10L),
        LocalDateTime.now().minusDays(1),
        LocalDateTime.now().plusDays(1));
    when(voteRepository.findById(42L)).thenReturn(Optional.of(vote));

    assertThatThrownBy(() -> voteService.getVoteResult(42L))
        .isInstanceOfSatisfying(BusinessException.class, exception -> {
          assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
          assertThat(exception.getFieldName()).isEqualTo("voteId");
          assertThat(exception.getMessage())
              .isEqualTo("투표가 종료된 후 결과를 조회할 수 있습니다.");
        });

    verifyNoInteractions(
        voteParticipationRepository,
        voteReceiptRepository,
        voteAgendaRepository,
        voteOptionRepository);
  }

  @Test
  void getVoteResultRejectsMissingVote() {
    when(voteRepository.findById(42L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> voteService.getVoteResult(42L))
        .isInstanceOfSatisfying(BusinessException.class, exception -> {
          assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
          assertThat(exception.getFieldName()).isEqualTo("voteId");
        });

    verifyNoInteractions(
        voteParticipationRepository,
        voteReceiptRepository,
        voteAgendaRepository,
        voteOptionRepository);
  }

  private static Vote vote(long id, String title, List<Long> permitByMember,
      LocalDateTime startAt, LocalDateTime endAt) {
    Vote vote = Vote.builder()
        .title(title)
        .description("설명")
        .permitByMember(permitByMember)
        .startAt(startAt)
        .endAt(endAt)
        .build();
    ReflectionTestUtils.setField(vote, "id", id);
    return vote;
  }

  private static VoteAgenda agenda(long id, Vote vote, String title, int displayOrder,
      int minSelect, int maxSelect) {
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

  private static VoteOption option(long id, VoteAgenda agenda, String content, int displayOrder) {
    VoteOption option = VoteOption.builder()
        .agenda(agenda)
        .content(content)
        .displayOrder(displayOrder)
        .build();
    ReflectionTestUtils.setField(option, "id", id);
    return option;
  }

  private static VoteParticipation participation(Vote vote, String realName, float generation) {
    return participation(vote, realName, generation, 2L);
  }

  private static VoteParticipation participation(
      Vote vote,
      String realName,
      float generation,
      long memberId
  ) {
    Member member = mock(Member.class);
    when(member.getId()).thenReturn(memberId);
    return VoteParticipation.builder()
        .vote(vote)
        .member(member)
        .voterNameSnapshot(realName)
        .voterGenerationSnapshot(generation)
        .build();
  }

  private static VoteReceipt receipt(
      Vote vote,
      String token,
      VoteOption... options
  ) {
    VoteReceipt receipt = VoteReceipt.builder()
        .vote(vote)
        .build();
    ReflectionTestUtils.setField(receipt, "token", UUID.fromString(token));
    for (VoteOption option : options) {
      receipt.getChoices().add(VoteChoice.builder()
          .receipt(receipt)
          .option(option)
          .build());
    }
    return receipt;
  }
}
