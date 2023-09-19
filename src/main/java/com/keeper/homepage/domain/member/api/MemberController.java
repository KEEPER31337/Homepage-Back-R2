package com.keeper.homepage.domain.member.api;

import com.keeper.homepage.domain.auth.application.CheckDuplicateService;
import com.keeper.homepage.domain.auth.application.EmailAuthService;
import com.keeper.homepage.domain.auth.dto.request.EmailAuthRequest;
import com.keeper.homepage.domain.auth.dto.response.EmailAuthResponse;
import com.keeper.homepage.domain.member.application.MemberProfileService;
import com.keeper.homepage.domain.member.application.MemberService;
import com.keeper.homepage.domain.member.dto.request.ChangePasswordRequest;
import com.keeper.homepage.domain.member.dto.request.ProfileUpdateRequest;
import com.keeper.homepage.domain.member.dto.request.UpdateMemberEmailAddressRequest;
import com.keeper.homepage.domain.member.dto.request.UpdateMemberTypeRequest;
import com.keeper.homepage.domain.member.dto.response.MemberPointRankResponse;
import com.keeper.homepage.domain.member.dto.response.MemberResponse;
import com.keeper.homepage.domain.member.dto.response.profile.MemberProfileResponse;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

  private final MemberService memberService;
  private final MemberProfileService memberProfileService;
  private final EmailAuthService emailAuthService;

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

  @PostMapping("/{otherId}/follow")
  public ResponseEntity<Void> follow(
      @LoginMember Member member,
      @PathVariable long otherId
  ) {
    memberService.follow(member, otherId);
    return ResponseEntity.status(HttpStatus.CREATED)
        .build();
  }

  @DeleteMapping("{otherId}/unfollow")
  public ResponseEntity<Void> unfollow(
      @LoginMember Member member,
      @PathVariable long otherId
  ) {
    memberService.unfollow(member, otherId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/profile")
  public ResponseEntity<Void> updateProfile(
      @LoginMember Member member,
      @RequestBody @Valid ProfileUpdateRequest request
  ) {
    memberProfileService.updateProfile(member, request.toEntity());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PatchMapping("/thumbnail")
  public ResponseEntity<Void> updateProfileThumbnail(
      @LoginMember Member member,
      @ModelAttribute MultipartFile thumbnail
  ) {
    memberProfileService.updateProfileThumbnail(member, thumbnail);

    return ResponseEntity.noContent()
        .build();
  }

  @GetMapping("/{memberId}/profile")
  public ResponseEntity<MemberProfileResponse> getMemberProfile(
      @LoginMember Member me,
      @PathVariable @PositiveOrZero long memberId
  ) {
    return ResponseEntity.ok(memberService.getMemberProfile(me, memberId));
  }

  @PatchMapping("/types/{typeId}")
  public ResponseEntity<Void> updateMemberType(
      @PathVariable long typeId,
      @RequestBody @Valid UpdateMemberTypeRequest request
  ) {
    memberService.updateMemberType(request.getMemberIds(), typeId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/email-auth")
  public ResponseEntity<String> emailAuth(
      @RequestBody @Valid EmailAuthRequest request
  ) {
    memberProfileService.checkDuplicateEmailAddress(request.getEmail());
    memberProfileService.sendEmailChangeAuthCode(request.getEmail());
    return ResponseEntity.ok("이메일 인증 번호를 보냈습니다.");
  }

  @PatchMapping("/email")
  public ResponseEntity<Void> updateMemberEmail(
      @LoginMember Member member,
      @RequestBody @Valid UpdateMemberEmailAddressRequest request
  ) {
    memberProfileService.updateProfileEmailAddress(member, request.getEmail(), request.getAuth(), request.getPassword());
    return ResponseEntity.noContent().build();
  }
}
