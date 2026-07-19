package com.keeper.homepage.global.config.security.session;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SessionLookupResult(
    SessionLookupStatus status,
    SessionData session,
    boolean touched,
    @JsonProperty("redis_time") long redisTime,
    @JsonProperty("expires_at") long expiresAt
) {

  public static SessionLookupResult invalid() {
    return new SessionLookupResult(SessionLookupStatus.INVALID, null, false, 0, 0);
  }

  public boolean isValid() {
    return status == SessionLookupStatus.VALID;
  }

  public long remainingTtlMillis() {
    return Math.max(0, expiresAt - redisTime);
  }
}
