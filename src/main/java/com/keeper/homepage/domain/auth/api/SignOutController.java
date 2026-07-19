package com.keeper.homepage.domain.auth.api;

import com.keeper.homepage.domain.auth.application.SignOutService;
import jakarta.servlet.http.HttpServletRequest;
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
  public ResponseEntity<Void> signOut(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse) {
    signOutService.signOut(httpServletRequest, httpServletResponse);
    return ResponseEntity.noContent().build();
  }
}
