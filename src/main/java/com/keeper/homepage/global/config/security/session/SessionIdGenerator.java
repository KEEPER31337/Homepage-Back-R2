package com.keeper.homepage.global.config.security.session;

import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class SessionIdGenerator {

  private static final int SESSION_ID_BYTES = 32;

  private final SecureRandom secureRandom = new SecureRandom();

  public String generate() {
    byte[] bytes = new byte[SESSION_ID_BYTES];
    secureRandom.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }
}
