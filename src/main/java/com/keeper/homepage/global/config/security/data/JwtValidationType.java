package com.keeper.homepage.global.config.security.data;

import lombok.Getter;

@Getter
public enum JwtValidationType {
  VALID("유효한 토큰입니다."),
  MALFORMED("손상된 토큰입니다."),
  EXPIRED("만료된 토큰입니다."),
  UNSUPPORTED("지원하지 않는 토큰입니다."),
  WRONG_SIGNATURE("시그니처 검증에 실패한 토큰입니다."),
  UNKNOWN("알 수 없는 이유로 유효하지 않은 토큰입니다."),
  EMPTY("토큰이 비어있습니다."),
  ;

  private final String msg;

  JwtValidationType(String msg) {
    this.msg = msg;
  }
}
