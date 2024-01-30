package com.keeper.homepage.global.config.security.filter;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.config.security.data.JwtType.REFRESH_TOKEN;
import static com.keeper.homepage.global.config.security.data.JwtValidationType.EXPIRED;
import static org.springframework.http.HttpHeaders.USER_AGENT;

import com.keeper.homepage.domain.auth.application.AuthCookieService;
import com.keeper.homepage.global.config.security.JwtTokenProvider;
import com.keeper.homepage.global.config.security.data.JwtValidationType;
import com.keeper.homepage.global.config.security.data.TokenValidationResultDto;
import com.keeper.homepage.global.util.redis.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Slf4j
@RequiredArgsConstructor
@Component
public class RefreshTokenFilter extends GenericFilterBean {

  private final JwtTokenProvider jwtTokenProvider;
  private final RedisUtil redisUtil;
  private final AuthCookieService authCookieService;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    final var accessTokenDto = jwtTokenProvider.tryCheckTokenValid(httpRequest, ACCESS_TOKEN);
    final var refreshTokenDto = jwtTokenProvider.tryCheckTokenValid(httpRequest, REFRESH_TOKEN);
    boolean isAccessExpiredAndRefreshValid = isAccessTokenExpired(accessTokenDto.getResultType()) &&
        isRefreshTokenValid(refreshTokenDto) &&
        isTokenInRedis(refreshTokenDto, httpRequest.getHeader(USER_AGENT));

    if (isAccessExpiredAndRefreshValid) {
      Authentication auth = jwtTokenProvider.getAuthentication(refreshTokenDto.getToken());
      SecurityContextHolder.getContext().setAuthentication(auth);

      String authId = String.valueOf(jwtTokenProvider.getAuthId(refreshTokenDto.getToken()));
      String[] roles = jwtTokenProvider.getRoles(refreshTokenDto.getToken());
      HttpServletResponse httpResponse = (HttpServletResponse) response;

      authCookieService.setNewCookieInResponse(authId, roles, httpRequest.getHeader(USER_AGENT), httpResponse);
    } else {
      authCookieService.setCookieExpired((HttpServletResponse) response);
    }

    filterChain.doFilter(request, response);
  }

  private boolean isTokenInRedis(TokenValidationResultDto refreshTokenDto, String userAgent) {
    long authId = jwtTokenProvider.getAuthId(refreshTokenDto.getToken());
    String refreshTokenKey = JwtTokenProvider.getRefreshTokenKeyForRedis(String.valueOf(authId), userAgent);
    Optional<String> tokenInRedis = redisUtil.getData(refreshTokenKey, String.class);
    return tokenInRedis.isPresent() && tokenInRedis.get().equals(refreshTokenDto.getToken());
  }

  private static boolean isAccessTokenExpired(JwtValidationType resultType) {
    return EXPIRED.equals(resultType);
  }

  private boolean isRefreshTokenValid(TokenValidationResultDto refreshTokenDto) {
    return refreshTokenDto.isValid();
  }
}
