package com.keeper.homepage.domain.ctf.api;

import com.keeper.homepage.domain.ctf.application.CtfContestService;
import com.keeper.homepage.domain.ctf.dto.request.contest.CreateContestRequest;
import com.keeper.homepage.domain.ctf.dto.request.contest.UpdateContestRequest;
import com.keeper.homepage.domain.ctf.dto.response.contest.CtfContestResponse;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@Secured("ROLE_회장")
@RequestMapping("/ctf/contests")
public class CtfContestController {

  private final CtfContestService ctfContestService;

  @PostMapping
  public ResponseEntity<Void> createContest(
      @LoginMember Member member,
      @RequestBody @Valid CreateContestRequest request
  ) {
    ctfContestService.createContest(member, request.getName(), request.getDescription());
    return ResponseEntity.status(HttpStatus.CREATED)
        .build();
  }

  @PutMapping("/{contestId}")
  public ResponseEntity<Void> updateContest(
      @PathVariable long contestId,
      @RequestBody @Valid UpdateContestRequest request
  ) {
    ctfContestService.updateContest(contestId, request.getName(), request.getDescription(), request.isJoinable());
    return ResponseEntity.status(HttpStatus.CREATED)
        .build();
  }

  @PatchMapping("/{contestId}/open")
  public ResponseEntity<Void> openContest(
      @PathVariable long contestId
  ) {
    ctfContestService.openContest(contestId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{contestId}/close")
  public ResponseEntity<Void> closeContest(
      @PathVariable long contestId
  ) {
    ctfContestService.closeContest(contestId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<Page<CtfContestResponse>> getContests(
      @RequestParam(defaultValue = "0") @PositiveOrZero int page,
      @RequestParam(defaultValue = "10") @PositiveOrZero int size
  ) {
    Page<CtfContestResponse> response = ctfContestService.getContests(PageRequest.of(page, size));
    return ResponseEntity.ok(response);
  }
}
