package com.keeper.homepage.domain.auth.api;

import com.keeper.homepage.global.config.security.annotation.AuthId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth-test")
public class AuthController {

  @GetMapping
  public String noToken() {
    return "ok";
  }

  @Secured("ROLE_회원")
  @GetMapping("/user")
  public String userToken(@AuthId long myId) {
    return String.valueOf(myId);
  }

  @Secured("ROLE_회장")
  @GetMapping("/admin")
  public String adminToken(@AuthId long myId) {
    return String.valueOf(myId);
  }

  @GetMapping("/refresh")
  public String refreshToken(@AuthId long myId) {
    return String.valueOf(myId);
  }
}
