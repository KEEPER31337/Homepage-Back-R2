package com.keeper.homepage.domain.ctf.api;

import com.keeper.homepage.domain.ctf.application.CtfTeamService;
import com.keeper.homepage.domain.ctf.dto.request.CreateTeamRequest;
import com.keeper.homepage.domain.ctf.dto.request.UpdateTeamRequest;
import com.keeper.homepage.domain.ctf.dto.response.CtfTeamDetailResponse;
import com.keeper.homepage.domain.ctf.dto.response.CtfTeamResponse;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ctf/teams")
public class CtfTeamController {

  private final CtfTeamService ctfTeamService;

  @PostMapping
  public ResponseEntity<Void> createTeam(
      @LoginMember Member member,
      @RequestBody @Valid CreateTeamRequest request
  ) {
    ctfTeamService.createTeam(member, request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .build();
  }

  @PutMapping("/{teamId}")
  public ResponseEntity<Void> updateTeam(
      @LoginMember Member member,
      @PathVariable long teamId,
      @RequestBody @Valid UpdateTeamRequest request
  ) {
    ctfTeamService.updateTeam(member, teamId, request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .build();
  }

  @GetMapping("/{teamId}")
  public ResponseEntity<CtfTeamDetailResponse> getTeam(
      @PathVariable long teamId
  ) {
    return ResponseEntity.ok(ctfTeamService.getTeam(teamId));
  }

  @GetMapping
  public ResponseEntity<Page<CtfTeamResponse>> getTeams(
      @RequestParam long contestId,
      @RequestParam(defaultValue = "") String search,
      @RequestParam(defaultValue = "0") @PositiveOrZero int page,
      @RequestParam(defaultValue = "10") @PositiveOrZero int size
  ) {
    return ResponseEntity.ok(ctfTeamService.getTeams(contestId, search, PageRequest.of(page, size)));
  }
}
