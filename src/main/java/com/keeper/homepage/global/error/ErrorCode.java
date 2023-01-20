package com.keeper.homepage.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 도메인은 주석으로 구분한다.
 */
@Getter
public enum ErrorCode {

  // USER
  USER_NOT_FOUND("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  ;

  private final String message;
  private final HttpStatus httpStatus;

  ErrorCode(String message, HttpStatus httpStatus) {
    this.message = message;
    this.httpStatus = httpStatus;
  }
}
