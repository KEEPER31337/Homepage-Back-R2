package com.keeper.homepage.domain.point.api;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.point.application.GivePointService;
import com.keeper.homepage.domain.point.application.PointLogService;
import com.keeper.homepage.domain.point.dto.request.presentPointRequest;
import com.keeper.homepage.domain.point.dto.response.FindAllPointLogsResponse;
import com.keeper.homepage.domain.point.entity.PointLog;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/points")
public class PointController {

  private final GivePointService givePointService;
  private final PointLogService pointLogService;

  @PostMapping("/present")
  public ResponseEntity<Void> presentPoint(@LoginMember Member member,
      @RequestBody @Valid presentPointRequest request) {
    givePointService.givePoint(member.getId(),
        request.getMemberId(),
        request.getPoint(),
        request.getMessage());

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping()
  public ResponseEntity<Page<FindAllPointLogsResponse>> findAllPointLogs(
      @RequestParam(defaultValue = "0") @PositiveOrZero int size,
      @RequestParam(defaultValue = "10") @PositiveOrZero @Max(30) int page
  ) {
    return ResponseEntity.ok(
        pointLogService.findAllPointLogs(PageRequest.of(size, page))
            .map(FindAllPointLogsResponse::from)
    );
  }
}
