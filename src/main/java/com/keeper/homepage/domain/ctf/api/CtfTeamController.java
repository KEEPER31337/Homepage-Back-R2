package com.keeper.homepage.domain.ctf.api;

import com.keeper.homepage.domain.ctf.application.CtfTeamService;
import com.keeper.homepage.domain.ctf.dto.request.CreateTeamRequest;
import com.keeper.homepage.domain.ctf.dto.request.UpdateTeamRequest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
