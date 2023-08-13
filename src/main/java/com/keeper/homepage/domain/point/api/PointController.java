package com.keeper.homepage.domain.point.api;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.point.application.GivePointService;
import com.keeper.homepage.domain.point.dto.request.presentPointRequest;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/points")
public class PointController {

  private final GivePointService givePointService;

  @PostMapping("/present")
  public ResponseEntity<Void> presentPoint(@LoginMember Member member,
      @RequestBody @Valid presentPointRequest request) {
    givePointService.givePoint(member.getId(),
        request.getMemberId(),
        request.getPoint(),
        request.getMessage());

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
