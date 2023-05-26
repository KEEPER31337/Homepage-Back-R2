package com.keeper.homepage.domain.member.api;

import com.keeper.homepage.domain.member.application.MemberRankService;
import com.keeper.homepage.domain.member.dto.response.MemberPointRankResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/members/ranking")
public class MemberRankController {

  private final MemberRankService memberRankService;

  @GetMapping("/point")
  public ResponseEntity<Page<MemberPointRankResponse>> getPointRanking(
      @RequestParam(defaultValue = "0") @PositiveOrZero int page,
      @RequestParam(defaultValue = "10") @PositiveOrZero int size
  ) {
    Page<MemberPointRankResponse> response = memberRankService.getPointRanking(PageRequest.of(page, size));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/game")
  public void getGameRanking() {
    // TODO: 게임 포인트 랭킹 구현
  }
}
