package com.keeper.homepage.domain.auth.api.test;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth-test")
public class AuthTestController {

  @GetMapping
  public String noToken() {
    return "ok";
  }

  @Secured("ROLE_회원")
  @GetMapping("/user")
  public String userToken(@LoginMember Member loginMember) {
    return String.valueOf(loginMember.getId());
  }

  @Secured("ROLE_회장")
  @GetMapping("/admin")
  public String adminToken(@LoginMember Member loginMember) {
    return String.valueOf(loginMember.getId());
  }

  @GetMapping("/refresh")
  public String refreshToken() {
    return "refresh!";
  }
}
