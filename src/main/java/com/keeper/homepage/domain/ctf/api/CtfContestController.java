package com.keeper.homepage.domain.ctf.api;

import com.keeper.homepage.domain.ctf.application.CtfContestService;
import com.keeper.homepage.domain.ctf.dto.request.CreateContestRequest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
