package com.keeper.homepage.domain.auth.application;

import com.keeper.homepage.domain.member.entity.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.keeper.homepage.global.config.security.data.JwtType.REFRESH_TOKEN;

@RequiredArgsConstructor
@Service
public class SignOutService {

  private final AuthCookieService authCookieService;

  public void signOut(Member me, HttpServletRequest request, HttpServletResponse response) {
    String refreshToken = resolveRefreshToken(request);
    authCookieService.setCookieExpiredWithRedis(String.valueOf(me.getId()), refreshToken, response);
  }

  private String resolveRefreshToken(HttpServletRequest request) {
    if (request.getCookies() == null) {
      return null;
    }
    Optional<String> refreshToken = Arrays.stream(request.getCookies())
        .filter(cookie -> REFRESH_TOKEN.getTokenName().equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst();
    return refreshToken.orElse(null);
  }
}
