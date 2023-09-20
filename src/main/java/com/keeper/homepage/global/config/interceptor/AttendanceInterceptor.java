package com.keeper.homepage.global.config.interceptor;

import static com.keeper.homepage.global.error.ErrorCode.MEMBER_NOT_FOUND;

import com.keeper.homepage.domain.attendance.application.AttendanceService;
import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.error.BusinessException;
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
import org.springframework.web.servlet.ModelAndView;

@Component
@RequiredArgsConstructor
public class AttendanceInterceptor implements HandlerInterceptor {

  private final AttendanceService attendanceService;
  private final MemberRepository memberRepository;
  private final RedisUtil redisUtil;

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      long memberId = Long.parseLong(authentication.getName());

      String key = "attendance:member:" + memberId;
      Optional<String> data = redisUtil.getData(key, String.class);
      if (data.isEmpty()) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(memberId, "memberId", MEMBER_NOT_FOUND));
        attendanceService.create(member);
      }
    }
  }
}
