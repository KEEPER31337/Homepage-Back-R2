package com.keeper.homepage.domain.member.api;

import com.keeper.homepage.domain.member.application.MemberService;
import com.keeper.homepage.domain.member.dto.request.ChangePasswordRequest;
import com.keeper.homepage.domain.member.dto.response.MemberPointRankResponse;
import com.keeper.homepage.domain.member.dto.response.memberProfile.MemberProfileResponse;
import com.keeper.homepage.domain.member.dto.response.MemberResponse;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

  private final MemberService memberService;

  @PatchMapping("/change-password")
  public ResponseEntity<Void> changePassword(
      @LoginMember Member me,
      @RequestBody @Valid ChangePasswordRequest request) {
    memberService.changePassword(me, request.getNewPassword());
    return ResponseEntity.noContent()
        .location(URI.create("/members/me"))
        .build();
  }

  // TODO: 비밀번호 변경 후 redirect 용 API, 임시로 만들어 둠
  @GetMapping("/me")
  public void getMyProfile() {
  }

  @GetMapping("/real-name")
  public ResponseEntity<List<MemberResponse>> getMembersByRealName(
      @RequestParam(required = false) String searchName
  ) {
    return ResponseEntity.ok(memberService.getMembersByRealName(searchName));
  }

  @GetMapping("/point-rank")
  public ResponseEntity<Page<MemberPointRankResponse>> getPointRanks(
      @RequestParam(defaultValue = "0") @PositiveOrZero int page,
      @RequestParam(defaultValue = "10") @PositiveOrZero int size
  ) {
    Page<MemberPointRankResponse> response = memberService.getPointRanking(PageRequest.of(page, size));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{memberId}/profile")
  public ResponseEntity<MemberProfileResponse> getMemberProfile(
      @PathVariable @PositiveOrZero long memberId
  ) {
    return ResponseEntity.ok(
        memberService.getMemberProfile(memberId)
    );
  }
}
