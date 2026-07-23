package com.keeper.homepage.domain.auth.application;

import static com.keeper.homepage.global.config.security.session.SessionPolicy.SESSION_COOKIE_NAME;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class AuthCookieService {

  public Optional<String> resolveSessionId(HttpServletRequest request) {
    if (request.getCookies() == null) {
      return Optional.empty();
    }
    return Arrays.stream(request.getCookies())
        .filter(cookie -> SESSION_COOKIE_NAME.equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst();
  }

  public void setSessionCookie(HttpServletResponse response, String sessionId,
      long maxAgeMillis) {
    long maxAgeSeconds = Math.max(1, Math.ceilDiv(maxAgeMillis, 1000));
    ResponseCookie cookie = ResponseCookie.from(SESSION_COOKIE_NAME, sessionId)
        .path("/")
        .sameSite("Strict")
        .httpOnly(true)
        .secure(true)
        .maxAge(Duration.ofSeconds(maxAgeSeconds))
        .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  public void expireSessionCookie(HttpServletResponse response) {
    ResponseCookie cookie = ResponseCookie.from(SESSION_COOKIE_NAME, "")
        .path("/")
        .sameSite("Strict")
        .httpOnly(true)
        .secure(true)
        .maxAge(Duration.ZERO)
        .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }
}
