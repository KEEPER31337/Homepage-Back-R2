package com.keeper.homepage.global.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keeper.homepage.domain.auth.application.AuthCookieService;
import com.keeper.homepage.domain.auth.application.SessionService;
import com.keeper.homepage.global.config.security.session.SessionAuthenticationFactory;
import com.keeper.homepage.global.config.security.session.SessionLookupResult;
import com.keeper.homepage.global.config.security.session.SessionLookupStatus;
import com.keeper.homepage.global.config.security.session.SessionStoreException;
import com.keeper.homepage.global.error.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionAuthenticationFilter extends OncePerRequestFilter {

  private final SessionService sessionService;
  private final AuthCookieService authCookieService;
  private final SessionAuthenticationFactory sessionAuthenticationFactory;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    var sessionId = authCookieService.resolveSessionId(request);
    if (sessionId.isEmpty()) {
      filterChain.doFilter(request, response);
      return;
    }

    SessionLookupResult result;
    try {
      result = sessionService.findAndTouch(sessionId.get());
    } catch (DataAccessException | SessionStoreException e) {
      log.error("Redis 세션 저장소에 접근하지 못했습니다.", e);
      writeServiceUnavailable(response);
      return;
    }

    if (result.isValid()) {
      SecurityContextHolder.getContext()
          .setAuthentication(sessionAuthenticationFactory.create(result.session()));
      if (result.touched()) {
        authCookieService.setSessionCookie(response, sessionId.get(), result.remainingTtlMillis());
      }
    } else if (result.status() == SessionLookupStatus.TTL_MISSING
        || result.status() == SessionLookupStatus.MALFORMED) {
      log.warn("비정상 Redis 세션을 삭제했습니다. status={}", result.status());
    }

    filterChain.doFilter(request, response);
  }

  private void writeServiceUnavailable(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    objectMapper.writeValue(response.getWriter(), ErrorResponse.from("인증 저장소를 사용할 수 없습니다."));
  }
}
