package com.keeper.homepage.domain.auth.api;

import com.keeper.homepage.domain.auth.application.CheckDuplicateService;
import com.keeper.homepage.domain.auth.application.EmailAuthService;
import com.keeper.homepage.domain.auth.application.SignUpService;
import com.keeper.homepage.domain.auth.dto.request.EmailAuthRequest;
import com.keeper.homepage.domain.auth.dto.request.SignUpRequest;
import com.keeper.homepage.domain.auth.dto.response.CheckDuplicateResponse;
import com.keeper.homepage.domain.auth.dto.response.EmailAuthResponse;
import com.keeper.homepage.domain.member.entity.embedded.LoginId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sign-up")
public class SignUpController {

  private final SignUpService signUpService;
  private final EmailAuthService emailAuthService;
  private final CheckDuplicateService checkDuplicateService;

  @PostMapping
  public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest request) {
    long memberId = signUpService.signUp(request.toMemberProfile(), request.getAuthCode());
    return ResponseEntity.created(URI.create("/members/" + memberId)).build();
  }

  @PostMapping("/email-auth")
  public ResponseEntity<EmailAuthResponse> emailAuth(@RequestBody @Valid EmailAuthRequest request) {
    int expiredSeconds = emailAuthService.emailAuth(request.getEmail());
    return ResponseEntity.ok(EmailAuthResponse.from(expiredSeconds));
  }

  @GetMapping("/exists/login-id")
  public ResponseEntity<CheckDuplicateResponse> checkDuplicateLoginId(
      @RequestParam @NotNull LoginId loginId) {
    boolean isDuplicate = checkDuplicateService.isDuplicateLoginId(loginId);
    return ResponseEntity.ok(CheckDuplicateResponse.from(isDuplicate));
  }

  @GetMapping("/exists/email")
  public ResponseEntity<CheckDuplicateResponse> checkDuplicateEmail(
      @RequestParam @NotNull String email) {
    boolean isDuplicate = checkDuplicateService.isDuplicateEmail(email);
    return ResponseEntity.ok(CheckDuplicateResponse.from(isDuplicate));
  }

  @GetMapping("/exists/student-id")
  public ResponseEntity<CheckDuplicateResponse> checkDuplicateStudentId(
      @RequestParam @NotNull String studentId) {
    boolean isDuplicate = checkDuplicateService.isDuplicateStudentID(studentId);
    return ResponseEntity.ok(CheckDuplicateResponse.from(isDuplicate));
  }
}
