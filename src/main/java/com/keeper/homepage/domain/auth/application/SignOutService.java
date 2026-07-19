package com.keeper.homepage.domain.auth.application;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignOutService {

  private final AuthCookieService authCookieService;
  private final SessionService sessionService;

  public void signOut(HttpServletRequest request, HttpServletResponse response) {
    authCookieService.resolveSessionId(request).ifPresent(sessionService::deleteSession);
    authCookieService.expireSessionCookie(response);
  }
}
