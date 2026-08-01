package com.keeper.homepage.domain.vote.api;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.vote.application.VoteService;
import com.keeper.homepage.domain.vote.dto.response.VoteListResponse;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@Secured("ROLE_회원")
@RequestMapping("/votes")
public class VoteController {

  private final VoteService voteService;

  @GetMapping
  public ResponseEntity<VoteListResponse> getVotes(
      @LoginMember Member member,
      @RequestParam
      @Min(value = 1000, message = "연도는 1000 이상이어야 합니다.")
      @Max(value = 9998, message = "연도는 9998 이하여야 합니다.")
      int year
  ) {
    return ResponseEntity.ok(voteService.getVotes(member, year));
  }
}
