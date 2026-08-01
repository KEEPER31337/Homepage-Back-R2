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
import com.keeper.homepage.domain.vote.dao.VoteRepository;
import com.keeper.homepage.domain.vote.dto.response.VoteDetailResponse;
import com.keeper.homepage.domain.vote.dto.response.VoteListResponse;
import com.keeper.homepage.domain.vote.entity.Vote;
import com.keeper.homepage.domain.vote.entity.VoteAgenda;
import com.keeper.homepage.domain.vote.entity.VoteOption;
import com.keeper.homepage.global.error.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
  private VoteAgendaRepository voteAgendaRepository;

  @Mock
  private VoteOptionRepository voteOptionRepository;

  @InjectMocks
  private VoteService voteService;

  @Test
  void getVotesReturnsParticipationStatusForEachVote() {
    Member member = mock(Member.class);
    when(member.getId()).thenReturn(10L);
    when(member.getJobs()).thenReturn(List.of("ROLE_회원"));
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime openStartAt = now.minusDays(1);
    LocalDateTime openEndAt = now.plusDays(1);
    LocalDateTime futureStartAt = now.plusDays(1);
    LocalDateTime futureEndAt = now.plusDays(2);
    Vote permittedByRoleVote = vote(
        5L, "역할 허용 투표", List.of("ROLE_회원"), List.of(), openStartAt, openEndAt);
    Vote permittedByMemberVote = vote(
        4L, "회원 허용 투표", List.of(), List.of(10L), openStartAt, openEndAt);
    Vote notPermittedVote = vote(
        3L, "권한 없는 투표", List.of(), List.of(), openStartAt, openEndAt);
    Vote submittedVote = vote(
        2L, "제출한 투표", List.of("ROLE_회원"), List.of(), openStartAt, openEndAt);
    Vote outsideVotingPeriodVote = vote(
        1L, "기간 외 투표", List.of("ROLE_회원"), List.of(), futureStartAt, futureEndAt);
    when(voteRepository
        .findAllByStartAtGreaterThanEqualAndStartAtLessThanOrderByStartAtDescIdDesc(
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2027, 1, 1, 0, 0)))
        .thenReturn(List.of(
            permittedByRoleVote,
            permittedByMemberVote,
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
    when(member.getJobs()).thenReturn(List.of("ROLE_회원"));
    Vote vote = vote(
        42L,
        "회장 선거",
        List.of("ROLE_회원"),
        List.of(),
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
    when(member.getJobs()).thenReturn(List.of("ROLE_회원"));
    Vote vote = vote(
        42L,
        "회장 선거",
        List.of("ROLE_회장"),
        List.of(),
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

  private static Vote vote(long id, String title, List<String> permitByRole,
      List<Long> permitByMember, LocalDateTime startAt, LocalDateTime endAt) {
    Vote vote = Vote.builder()
        .title(title)
        .description("설명")
        .permitByRole(permitByRole)
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
}
