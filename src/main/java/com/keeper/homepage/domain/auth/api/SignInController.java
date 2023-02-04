package com.keeper.homepage.domain.auth.api;

import com.keeper.homepage.domain.auth.application.SignInService;
import com.keeper.homepage.domain.auth.dto.request.SignInRequest;
import com.keeper.homepage.domain.member.entity.embedded.LoginId;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<Void> signIn(@RequestBody @Valid SignInRequest request,
      HttpServletResponse httpServletResponse) {
    signInService.signIn(LoginId.from(request.getLoginId()),
        request.getRawPassword(), httpServletResponse);
    return ResponseEntity.noContent().build();
  }
}
