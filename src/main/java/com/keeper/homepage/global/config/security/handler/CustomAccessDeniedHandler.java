package com.keeper.homepage.global.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keeper.homepage.global.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException exception) throws IOException {
    val errorResponse = ErrorResponse.from("권한이 없습니다.");
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    response.sendError(HttpServletResponse.SC_FORBIDDEN);
  }
}