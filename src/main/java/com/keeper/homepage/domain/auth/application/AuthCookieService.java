package com.keeper.homepage.domain.auth.application;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.config.security.data.JwtType.REFRESH_TOKEN;

import com.keeper.homepage.global.config.security.JwtTokenProvider;
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
    setTokenInCookie(response, newRefreshToken, (int) REFRESH_TOKEN.getExpiredMillis() / 1000,
        REFRESH_TOKEN.getTokenName());
    String newAccessToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, authId, roles);
    setTokenInCookie(response, newAccessToken, (int) ACCESS_TOKEN.getExpiredMillis() / 1000,
        ACCESS_TOKEN.getTokenName());
    redisUtil.setDataExpire(authId, newRefreshToken, REFRESH_TOKEN.getExpiredMillis());
  }

  private void setTokenInCookie(HttpServletResponse httpResponse, String token,
      int expiredSeconds, String cookieName) {
    Cookie cookie = new Cookie(cookieName, token);
    cookie.setHttpOnly(true);
    cookie.setMaxAge(expiredSeconds);
    httpResponse.addCookie(cookie);
  }

  public void setCookieExpired(String authId, HttpServletResponse response) {
    setTokenInCookie(response, "", 0, REFRESH_TOKEN.getTokenName());
    setTokenInCookie(response, "", 0, ACCESS_TOKEN.getTokenName());
    redisUtil.deleteData(authId);
  }
}
