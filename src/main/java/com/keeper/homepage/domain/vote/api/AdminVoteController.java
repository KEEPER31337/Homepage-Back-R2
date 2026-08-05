package com.keeper.homepage.domain.vote.api;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.vote.application.AdminVoteService;
import com.keeper.homepage.domain.vote.dto.request.VoteCreateRequest;
import com.keeper.homepage.domain.vote.dto.response.AdminVoteListResponse;
import com.keeper.homepage.domain.vote.dto.response.VoteIdResponse;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@Secured({"ROLE_회장", "ROLE_부회장"})
@RequestMapping("/admin/votes")
public class AdminVoteController {

  private final AdminVoteService adminVoteService;

  @GetMapping
  public ResponseEntity<AdminVoteListResponse> getVotes(
      @RequestParam
      @Min(value = 1000, message = "연도는 1000 이상이어야 합니다.")
      @Max(value = 9998, message = "연도는 9998 이하여야 합니다.")
      int year
  ) {
    return ResponseEntity.ok(adminVoteService.getVotes(year));
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<VoteIdResponse> createVote(
      @LoginMember Member creator,
      @Valid @RequestBody VoteCreateRequest request
  ) {
    long voteId = adminVoteService.createVote(creator, request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new VoteIdResponse(voteId));
  }

  @DeleteMapping("/{voteId}")
  public ResponseEntity<Void> deleteVote(
      @PathVariable long voteId
  ) {
    adminVoteService.deleteVote(voteId);
    return ResponseEntity.noContent().build();
  }
}
