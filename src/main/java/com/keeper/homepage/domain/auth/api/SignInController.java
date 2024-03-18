package com.keeper.homepage.domain.auth.api;

import com.keeper.homepage.domain.auth.application.SignInService;
import com.keeper.homepage.domain.auth.dto.request.ChangePasswordForMissingRequest;
import com.keeper.homepage.domain.auth.dto.request.FindLoginIdRequest;
import com.keeper.homepage.domain.auth.dto.request.MemberIdAndEmailRequest;
import com.keeper.homepage.domain.auth.dto.request.SignInRequest;
import com.keeper.homepage.domain.auth.dto.response.CheckAuthCodeResponse;
import com.keeper.homepage.domain.auth.dto.response.EmailAuthResponse;
import com.keeper.homepage.domain.auth.dto.response.SignInResponse;
import com.keeper.homepage.domain.member.entity.embedded.EmailAddress;
import com.keeper.homepage.domain.member.entity.embedded.LoginId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sign-in")
public class SignInController {

  private final SignInService signInService;

  @PostMapping
  public ResponseEntity<SignInResponse> signIn(@RequestBody @Valid SignInRequest request,
      HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    return ResponseEntity.ok(
        signInService.signIn(
            LoginId.from(request.getLoginId()),
            request.getRawPassword(),
            httpRequest,
            httpResponse));
  }

  @PostMapping("/find-login-id")
  public ResponseEntity<Void> findLoginId(@RequestBody @Email FindLoginIdRequest request) {
    signInService.findLoginId(EmailAddress.from(request.getEmail()));
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/send-password-change-auth-code")
  public ResponseEntity<EmailAuthResponse> sendPasswordChangeAuthCode(
      @RequestBody @Valid MemberIdAndEmailRequest request) {
    int expiredSeconds = signInService.sendPasswordChangeAuthCode(
        EmailAddress.from(request.getEmail()),
        LoginId.from(request.getLoginId()));
    return ResponseEntity.ok(EmailAuthResponse.from(expiredSeconds));
  }

  @GetMapping("/check-auth-code")
  public ResponseEntity<CheckAuthCodeResponse> checkAuthCode(String email, String loginId,
      String authCode) {
    boolean isAuth = signInService.isAuthenticated(EmailAddress.from(email), LoginId.from(loginId),
        authCode);
    return ResponseEntity.ok(CheckAuthCodeResponse.from(isAuth));
  }

  @PatchMapping("/change-password-for-missing")
  public ResponseEntity<Void> changePassword(
      @RequestBody @Valid ChangePasswordForMissingRequest request) {
    signInService.changePassword(request.getAuthCode(),
        LoginId.from(request.getLoginId()), EmailAddress.from(request.getEmail()),
        request.getRawPassword());
    return ResponseEntity.noContent().build();
  }
}
