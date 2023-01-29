package com.keeper.homepage.domain.auth.api;

import com.keeper.homepage.domain.auth.application.EmailAuthService;
import com.keeper.homepage.domain.auth.application.SignUpService;
import com.keeper.homepage.domain.auth.dto.request.EmailAuthRequest;
import com.keeper.homepage.domain.auth.dto.request.SignUpRequest;
import com.keeper.homepage.domain.auth.dto.response.EmailAuthResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sign-up")
public class SignUpController {

  private final SignUpService signUpService;
  private final EmailAuthService emailAuthService;

  @PostMapping
  public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest request) {
  long memberId = signUpService.signUp(request);
    return ResponseEntity.created(URI.create("/members/" + memberId)).build();
  }

  @PostMapping("/email-auth")
  public ResponseEntity<EmailAuthResponse> emailAuth(@RequestBody @Valid EmailAuthRequest request) {
    int expiredSeconds = emailAuthService.emailAuth(request.getEmail());
    return ResponseEntity.ok(EmailAuthResponse.from(expiredSeconds));
  }
}
