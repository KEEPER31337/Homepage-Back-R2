package com.keeper.homepage.domain.vote.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.vote.application.AdminVoteService;
import com.keeper.homepage.domain.vote.dto.request.VoteAgendaCreateRequest;
import com.keeper.homepage.domain.vote.dto.request.VoteCreateRequest;
import com.keeper.homepage.domain.vote.dto.request.VoteOptionCreateRequest;
import com.keeper.homepage.domain.vote.dto.response.AdminVoteListResponse;
import com.keeper.homepage.domain.vote.dto.response.VoteIdResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AdminVoteControllerTest {

  @Mock
  private AdminVoteService adminVoteService;

  @InjectMocks
  private AdminVoteController adminVoteController;

  @Test
  void getVotesReturnsAdminVoteList() {
    AdminVoteListResponse expected = new AdminVoteListResponse(List.of());
    when(adminVoteService.getVotes()).thenReturn(expected);

    ResponseEntity<AdminVoteListResponse> response = adminVoteController.getVotes();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isSameAs(expected);
    verify(adminVoteService).getVotes();
  }

  @Test
  void createVoteReturnsCreatedVoteId() {
    Member creator = mock(Member.class);
    VoteCreateRequest request = validRequest();
    when(adminVoteService.createVote(creator, request)).thenReturn(42L);

    ResponseEntity<VoteIdResponse> response = adminVoteController.createVote(creator, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isEqualTo(new VoteIdResponse(42L));
    verify(adminVoteService).createVote(creator, request);
  }

  @Test
  void deleteVoteReturnsNoContent() {
    ResponseEntity<Void> response = adminVoteController.deleteVote(42L);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response.getBody()).isNull();
    verify(adminVoteService).deleteVote(42L);
  }

  private static VoteCreateRequest validRequest() {
    return new VoteCreateRequest(
        "2026년 회장 선거",
        null,
        List.of(16381L),
        LocalDateTime.of(2026, 8, 1, 0, 0),
        LocalDateTime.of(2026, 8, 2, 0, 0),
        List.of(new VoteAgendaCreateRequest(
            "회장 선출",
            1,
            1,
            List.of(new VoteOptionCreateRequest("후보 A"))
        ))
    );
  }
}
