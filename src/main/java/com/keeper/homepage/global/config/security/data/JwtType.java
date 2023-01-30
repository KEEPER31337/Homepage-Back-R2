package com.keeper.homepage.global.config.security.data;


import lombok.Getter;

@Getter
public enum JwtType {
  ACCESS_TOKEN("accessToken", 60 * 60 * 1000L), // 1시간
  REFRESH_TOKEN("refreshToken", 14 * 24 * 60 * 60 * 1000L), // 2주
  ;

  private final String tokenName;
  private final long expiredMillis;

  JwtType(String tokenName, long expiredMillis) {
    this.tokenName = tokenName;
    this.expiredMillis = expiredMillis;
  }
}
