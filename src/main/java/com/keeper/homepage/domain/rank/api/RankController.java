package com.keeper.homepage.domain.rank.api;

import com.keeper.homepage.domain.rank.application.RankService;
import com.keeper.homepage.domain.rank.dto.response.ContinuousAttendanceResponse;
import com.keeper.homepage.domain.rank.dto.response.PointRankResponse;
import com.keeper.homepage.global.util.response.ListResponse;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
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
@RequestMapping("/ranking")
public class RankController {

  private final RankService memberRankService;

  @GetMapping("/point")
  public ResponseEntity<Page<PointRankResponse>> getPointRanking(
      @RequestParam(defaultValue = "0") @PositiveOrZero int page,
      @RequestParam(defaultValue = "10") @PositiveOrZero int size
  ) {
    Page<PointRankResponse> response = memberRankService.getPointRanking(PageRequest.of(page, size));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/game")
  public void getGameRanking() {
    // TODO: 게임 포인트 랭킹 구현
  }

  @GetMapping("/continuous-attendance")
  public ResponseEntity<ListResponse<List<ContinuousAttendanceResponse>>> getContinuousAttendance() {
    List<ContinuousAttendanceResponse> responses = memberRankService.getContinuousAttendance();
    return ResponseEntity.ok(new ListResponse<>(responses));
  }
}
