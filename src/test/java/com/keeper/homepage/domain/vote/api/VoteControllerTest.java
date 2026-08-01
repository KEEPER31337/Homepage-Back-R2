package com.keeper.homepage.domain.vote.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.vote.application.VoteParticipationService;
import com.keeper.homepage.domain.vote.application.VoteService;
import com.keeper.homepage.domain.vote.dto.request.VoteParticipationRequest;
import com.keeper.homepage.domain.vote.dto.request.VoteReceiptCheckRequest;
import com.keeper.homepage.domain.vote.dto.request.VoteSelectionRequest;
import com.keeper.homepage.domain.vote.dto.response.VoteDetailResponse;
import com.keeper.homepage.domain.vote.dto.response.VoteListResponse;
import com.keeper.homepage.domain.vote.dto.response.VoteReceiptCheckResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class VoteControllerTest {

  @Mock
  private VoteService voteService;

  @Mock
  private VoteParticipationService voteParticipationService;

  @InjectMocks
  private VoteController voteController;

  @Test
  void getVotesReturnsVoteList() {
    Member member = mock(Member.class);
    VoteListResponse expected = new VoteListResponse(List.of());
    when(voteService.getVotes(member, 2026)).thenReturn(expected);

    ResponseEntity<VoteListResponse> response = voteController.getVotes(member, 2026);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isSameAs(expected);
    verify(voteService).getVotes(member, 2026);
  }

  @Test
  void getVoteReturnsVoteDetail() {
    Member member = mock(Member.class);
    VoteDetailResponse expected = new VoteDetailResponse(
        42L,
        "회장 선거",
        "설명",
        LocalDateTime.of(2026, 8, 1, 0, 0),
        LocalDateTime.of(2026, 8, 2, 0, 0),
        List.of()
    );
    when(voteService.getVote(member, 42L)).thenReturn(expected);

    ResponseEntity<VoteDetailResponse> response = voteController.getVote(member, 42L);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isSameAs(expected);
    verify(voteService).getVote(member, 42L);
  }

  @Test
  void participateReturnsOkWithoutBody() {
    Member member = mock(Member.class);
    VoteParticipationRequest request = new VoteParticipationRequest(
        UUID.fromString("123e4567-e89b-42d3-a456-426614174000"),
        List.of(new VoteSelectionRequest(20L, List.of(101L)))
    );

    ResponseEntity<Void> response = voteController.participate(member, 42L, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNull();
    verify(voteParticipationService).participate(member, 42L, request);
  }

  @Test
  void checkReceiptReturnsSelectedOptions() {
    VoteReceiptCheckRequest request = new VoteReceiptCheckRequest(
        UUID.fromString("123e4567-e89b-42d3-a456-426614174000"));
    VoteReceiptCheckResponse expected = new VoteReceiptCheckResponse(42L, List.of());
    when(voteParticipationService.checkReceipt(request)).thenReturn(expected);

    ResponseEntity<VoteReceiptCheckResponse> response = voteController.checkReceipt(request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isSameAs(expected);
    verify(voteParticipationService).checkReceipt(request);
  }
}
