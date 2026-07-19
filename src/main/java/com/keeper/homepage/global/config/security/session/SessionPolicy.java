package com.keeper.homepage.global.config.security.session;

import java.time.Duration;

public final class SessionPolicy {

  public static final String SESSION_COOKIE_NAME = "session_id";
  public static final String REDIS_KEY_PREFIX = "session:";
  public static final Duration IDLE_TIMEOUT = Duration.ofDays(7);
  public static final Duration TOUCH_THRESHOLD = IDLE_TIMEOUT.dividedBy(2);
  public static final Duration ABSOLUTE_TIMEOUT = Duration.ofDays(30);

  private SessionPolicy() {
  }
}
