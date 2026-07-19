package com.keeper.homepage.global.config.security.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record SessionData(
    @JsonProperty("user_id") long userId,
    @JsonProperty("created_at") long createdAt,
    @JsonProperty("absolute_expires_at") long absoluteExpiresAt,
    List<String> roles
) {

  public SessionData {
    roles = List.copyOf(roles);
  }
}
