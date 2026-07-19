package com.keeper.homepage.global.config.security.session;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class SessionAuthenticationFactory {

  public Authentication create(SessionData session) {
    SessionUserDetails userDetails = new SessionUserDetails(session.userId(), session.roles());
    return new UsernamePasswordAuthenticationToken(
        userDetails, "", userDetails.getAuthorities());
  }
}
