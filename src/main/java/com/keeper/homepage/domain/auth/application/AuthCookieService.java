package com.keeper.homepage.domain.auth.application;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.config.security.data.JwtType.REFRESH_TOKEN;

import com.keeper.homepage.global.config.security.JwtTokenProvider;
import com.keeper.homepage.global.config.security.data.JwtType;
import com.keeper.homepage.global.util.redis.RedisUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthCookieService {

  private final JwtTokenProvider jwtTokenProvider;
  private final RedisUtil redisUtil;

  public void setNewCookieInResponse(String authId, String[] roles, HttpServletResponse response) {
    String newRefreshToken = jwtTokenProvider.createAccessToken(REFRESH_TOKEN, authId, roles);
    setTokenInCookie(REFRESH_TOKEN, response, newRefreshToken);
    String newAccessToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, authId, roles);
    setTokenInCookie(ACCESS_TOKEN, response, newAccessToken);
    redisUtil.setDataExpire(authId, newRefreshToken, REFRESH_TOKEN.getExpiredMillis());
  }

  private void setTokenInCookie(JwtType jwtType, HttpServletResponse httpResponse, String token) {
    Cookie cookie = new Cookie(jwtType.getTokenName(), token);
    cookie.setHttpOnly(true);
    cookie.setMaxAge((int) jwtType.getExpiredMillis() / 1000);
    httpResponse.addCookie(cookie);
  }
}
