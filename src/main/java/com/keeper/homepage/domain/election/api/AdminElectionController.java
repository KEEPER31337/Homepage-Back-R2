package com.keeper.homepage.domain.election.api;

import com.keeper.homepage.domain.election.application.AdminElectionService;
import com.keeper.homepage.domain.election.dto.request.ElectionCreateRequest;
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
@RequestMapping("/admin/elections")
@Secured("ROLE_회장")
public class AdminElectionController {

  private final AdminElectionService adminElectionService;

  @PostMapping
  public ResponseEntity<Void> createElection(
      @LoginMember Member member,
      @RequestBody @Valid ElectionCreateRequest request) {
    adminElectionService.createElection(member, request.getName(), request.getDescription(), request.getIsAvailable());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

}
