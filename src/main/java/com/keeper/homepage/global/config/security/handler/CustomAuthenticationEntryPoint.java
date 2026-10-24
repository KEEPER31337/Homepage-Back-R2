package com.keeper.homepage.global.config.security.handler;

import com.keeper.homepage.global.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException {
    val errorResponse = ErrorResponse.from("로그인이 필요합니다.");
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
  }
}
