package com.keeper.homepage.domain.auth.api;

import com.keeper.homepage.domain.auth.application.SignOutService;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sign-out")
public class SignOutController {

  private final SignOutService signOutService;

  @PostMapping
  public ResponseEntity<Void> signOut(@LoginMember Member me,
      HttpServletResponse httpServletResponse) {
    signOutService.signOut(me, httpServletResponse);
    return ResponseEntity.noContent().build();
  }
}
