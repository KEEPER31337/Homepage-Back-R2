package com.keeper.homepage.domain.auth.application;

import com.keeper.homepage.domain.member.entity.Member;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SignOutService {

  private final AuthCookieService authCookieService;

  public void signOut(Member me, HttpServletResponse response) {
    authCookieService.setCookieExpired(String.valueOf(me.getId()), response);
  }
}
