package com.keeper.homepage.global.config.security.session;

public enum SessionLookupStatus {
  VALID,
  INVALID,
  EXPIRED,
  TTL_MISSING,
  MALFORMED
}
