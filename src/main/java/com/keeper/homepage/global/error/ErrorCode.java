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
  AUTH_CODE_EXPIRED("인증 코드가 없거나 만료되었습니다.", HttpStatus.NOT_FOUND),
  AUTH_CODE_MISMATCH("인증 코드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  // MEMBER
  MEMBER_NOT_FOUND("해당 회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  MEMBER_EMAIL_DUPLICATE("회원 이메일이 중복됩니다.", HttpStatus.CONFLICT),
  MEMBER_LOGIN_ID_DUPLICATE("회원의 로그인 아이디가 중복됩니다.", HttpStatus.CONFLICT),
  MEMBER_STUDENT_ID_DUPLICATE("회원의 학번이 중복됩니다.", HttpStatus.CONFLICT),
  ;

  private final String message;
  private final HttpStatus httpStatus;

  ErrorCode(String message, HttpStatus httpStatus) {
    this.message = message;
    this.httpStatus = httpStatus;
  }
}
