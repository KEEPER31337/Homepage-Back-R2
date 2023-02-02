package com.keeper.homepage.domain.auth.api;

import com.keeper.homepage.domain.auth.application.SignInService;
import com.keeper.homepage.domain.auth.dto.request.SignInRequest;
import com.keeper.homepage.domain.member.entity.embedded.EmailAddress;
import com.keeper.homepage.domain.member.entity.embedded.LoginId;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/sign-in")
public class SignInController {

  private final SignInService signInService;

  @PostMapping
  public ResponseEntity<Void> signIn(@RequestBody @Valid SignInRequest request,
      HttpServletResponse httpServletResponse) {
    signInService.signIn(LoginId.from(request.getLoginId()),
        request.getRawPassword(), httpServletResponse);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/find-login-id")
  public ResponseEntity<Void> findLoginId(@RequestParam @Email String email) {
    signInService.findLoginId(EmailAddress.from(email));
    return ResponseEntity.noContent().build();
  }
}
