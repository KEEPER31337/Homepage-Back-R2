package com.keeper.homepage.domain.member.api;

import com.keeper.homepage.domain.member.application.MemberJobService;
import com.keeper.homepage.domain.member.dto.response.MemberJobResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Secured("ROLE_회장")
public class MemberJobController {

  private final MemberJobService memberJobService;

  @GetMapping("/executives")
  public ResponseEntity<List<MemberJobResponse>> getExecutives() {
    return ResponseEntity.ok(memberJobService.getExecutives());
  }

  @PostMapping("{memberId}/jobs/{jobId}")
  public ResponseEntity<Void> addMemberJob(
      @PathVariable Long memberId,
      @PathVariable Long jobId
  ) {
    memberJobService.addMemberJob(memberId, jobId);
    return ResponseEntity.status(HttpStatus.CREATED)
        .build();
  }

  @DeleteMapping("{memberId}/jobs/{jobId}")
  public ResponseEntity<Void> deleteMemberJob(
      @PathVariable Long memberId,
      @PathVariable Long jobId
  ) {
    memberJobService.deleteMemberJob(memberId, jobId);
    return ResponseEntity.noContent().build();
  }
}