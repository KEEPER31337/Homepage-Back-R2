package com.keeper.homepage.global.config.interceptor;

import com.keeper.homepage.domain.attendance.application.AttendanceService;
import com.keeper.homepage.global.util.redis.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AttendanceInterceptor implements HandlerInterceptor {

  private final AttendanceService attendanceService;
  private final RedisUtil redisUtil;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      long memberId = Long.parseLong(authentication.getName());

      String key = "attendance:member:" + memberId;
      Optional<String> data = redisUtil.getData(key, String.class);
      if (data.isEmpty()) {
        attendanceService.create(memberId);
      }
    }
    return true;
  }
}
