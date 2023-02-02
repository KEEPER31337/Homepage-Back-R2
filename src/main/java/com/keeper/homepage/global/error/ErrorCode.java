package com.keeper.homepage.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 도메인은 주석으로 구분한다.
 */
@Getter
public enum ErrorCode {

  // AUTH
  TOKEN_NOT_AVAILABLE("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
  // MEMBER
  MEMBER_NOT_FOUND("해당 회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  // SEMINAR
  SEMINAR_TYPE_NOT_FOUND("존재하지 않는 세미나 유형입니다.", HttpStatus.NOT_FOUND)
  ;

  private final String message;
  private final HttpStatus httpStatus;

  ErrorCode(String message, HttpStatus httpStatus) {
    this.message = message;
    this.httpStatus = httpStatus;
  }
}
