package com.keeper.homepage.domain.vote.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.vote.dao.VoteAgendaRepository;
import com.keeper.homepage.domain.vote.dao.VoteOptionRepository;
import com.keeper.homepage.domain.vote.dao.VoteRepository;
import com.keeper.homepage.domain.vote.dto.request.VoteAgendaCreateRequest;
import com.keeper.homepage.domain.vote.dto.request.VoteCreateRequest;
import com.keeper.homepage.domain.vote.dto.request.VoteOptionCreateRequest;
import com.keeper.homepage.domain.vote.entity.Vote;
import com.keeper.homepage.domain.vote.entity.VoteAgenda;
import com.keeper.homepage.domain.vote.entity.VoteOption;
import com.keeper.homepage.global.error.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AdminVoteServiceTest {

  @Mock
  private VoteRepository voteRepository;

  @Mock
  private VoteAgendaRepository voteAgendaRepository;

  @Mock
  private VoteOptionRepository voteOptionRepository;

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private AdminVoteService adminVoteService;

  @Captor
  private ArgumentCaptor<Vote> voteCaptor;

  @Captor
  private ArgumentCaptor<Iterable<VoteAgenda>> agendasCaptor;

  @Captor
  private ArgumentCaptor<Iterable<VoteOption>> optionsCaptor;

  private Member creator;

  @BeforeEach
  void setUp() {
    creator = mock(Member.class);
  }

  @Test
  void createVoteSavesVoteAgendasAndOptions() {
    VoteCreateRequest request = validRequest();
    Member permittedMember1 = memberWithId(16381L);
    Member permittedMember2 = memberWithId(26381L);
    when(memberRepository.findAllById(any()))
        .thenReturn(List.of(permittedMember1, permittedMember2));
    when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> {
      Vote vote = invocation.getArgument(0);
      ReflectionTestUtils.setField(vote, "id", 42L);
      return vote;
    });

    long voteId = adminVoteService.createVote(creator, request);

    assertThat(voteId).isEqualTo(42L);
    verify(voteRepository).save(voteCaptor.capture());
    verify(voteAgendaRepository).saveAll(agendasCaptor.capture());
    verify(voteOptionRepository).saveAll(optionsCaptor.capture());

    Vote vote = voteCaptor.getValue();
    assertThat(vote.getTitle()).isEqualTo(request.title());
    assertThat(vote.getDescription()).isEqualTo(request.description());
    assertThat(vote.getPermitByMember()).containsExactly(16381L, 26381L);
    assertThat(vote.getStartAt()).isEqualTo(request.startAt());
    assertThat(vote.getEndAt()).isEqualTo(request.endAt());
    assertThat(vote.getCreatedBy()).isSameAs(creator);

    List<VoteAgenda> agendas = toList(agendasCaptor.getValue());
    assertThat(agendas).hasSize(2);
    assertThat(agendas)
        .extracting(VoteAgenda::getDisplayOrder)
        .containsExactly(0, 1);
    assertThat(agendas)
        .extracting(VoteAgenda::getVote)
        .containsOnly(vote);

    List<VoteOption> options = toList(optionsCaptor.getValue());
    assertThat(options).hasSize(3);
    assertThat(options)
        .extracting(VoteOption::getDisplayOrder)
        .containsExactly(0, 1, 0);
    assertThat(options.get(0).getAgenda()).isSameAs(agendas.get(0));
    assertThat(options.get(1).getAgenda()).isSameAs(agendas.get(0));
    assertThat(options.get(2).getAgenda()).isSameAs(agendas.get(1));
  }

  @Test
  void createVoteRejectsMissingPermitMemberBeforeSaving() {
    VoteCreateRequest request = validRequest();
    Member permittedMember1 = memberWithId(16381L);
    when(memberRepository.findAllById(any())).thenReturn(List.of(permittedMember1));

    assertThatThrownBy(() -> adminVoteService.createVote(creator, request))
        .isInstanceOfSatisfying(BusinessException.class, exception -> {
          assertThat(exception.getFieldName()).isEqualTo("permitByUserIds");
          assertThat(exception.getInvalidValue()).contains("26381");
        });

    verify(voteRepository, never()).save(any());
    verifyNoInteractions(voteAgendaRepository, voteOptionRepository);
  }

  @Test
  void deleteVoteHardDeletesVote() {
    Vote vote = mock(Vote.class);
    when(voteRepository.findById(42L)).thenReturn(Optional.of(vote));

    adminVoteService.deleteVote(42L);

    verify(voteRepository).delete(vote);
  }

  @Test
  void deleteVoteRejectsMissingVote() {
    when(voteRepository.findById(42L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> adminVoteService.deleteVote(42L))
        .isInstanceOfSatisfying(BusinessException.class, exception -> {
          assertThat(exception.getFieldName()).isEqualTo("voteId");
          assertThat(exception.getInvalidValue()).isEqualTo("42");
        });

    verify(voteRepository, never()).delete(any());
  }

  private static Member memberWithId(long id) {
    Member member = mock(Member.class);
    when(member.getId()).thenReturn(id);
    return member;
  }

  private static <T> List<T> toList(Iterable<T> values) {
    return StreamSupport.stream(values.spliterator(), false).toList();
  }

  private static VoteCreateRequest validRequest() {
    return new VoteCreateRequest(
        "2026년 회장 선거",
        "2026년도 임원진을 선출하기 위한 투표입니다.",
        List.of(16381L, 26381L),
        LocalDateTime.of(2026, 8, 1, 0, 0),
        LocalDateTime.of(2026, 8, 2, 0, 0),
        List.of(
            new VoteAgendaCreateRequest(
                "회장 선출",
                1,
                1,
                List.of(
                    new VoteOptionCreateRequest("후보 A"),
                    new VoteOptionCreateRequest("후보 B")
                )
            ),
            new VoteAgendaCreateRequest(
                "부회장 선출",
                1,
                1,
                List.of(new VoteOptionCreateRequest("후보 C"))
            )
        )
    );
  }
}
