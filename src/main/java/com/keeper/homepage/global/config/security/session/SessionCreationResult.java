package com.keeper.homepage.global.config.security.session;

public record SessionCreationResult(
    String sessionId,
    SessionData session,
    long expiresAt
) {

  public long maxAgeMillis() {
    return expiresAt - session.createdAt();
  }
}
