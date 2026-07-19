package com.keeper.homepage.global.config.security.session;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StoredSessionCreationResult(
    Status status,
    SessionData session,
    @JsonProperty("expires_at") long expiresAt
) {

  public enum Status {
    CREATED,
    COLLISION
  }
}
