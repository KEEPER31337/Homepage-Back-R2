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
  TOO_MANY_REQUEST_AUTH_CODE("인증 코드가 만료되기 전에 다시 보내실 수 없습니다.", HttpStatus.BAD_REQUEST),
  AUTH_CODE_EXPIRED("인증 코드가 없거나 만료되었습니다.", HttpStatus.NOT_FOUND),
  AUTH_CODE_MISMATCH("인증 코드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  // MEMBER
  MEMBER_NOT_FOUND("해당 회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  MEMBER_EMAIL_DUPLICATE("회원 이메일이 중복됩니다.", HttpStatus.CONFLICT),
  MEMBER_LOGIN_ID_DUPLICATE("회원의 로그인 아이디가 중복됩니다.", HttpStatus.CONFLICT),
  MEMBER_STUDENT_ID_DUPLICATE("회원의 학번이 중복됩니다.", HttpStatus.CONFLICT),
  MEMBER_WRONG_ID_OR_PASSWORD("아이디 혹은 비밀번호가 잘못되었습니다.", HttpStatus.BAD_REQUEST),
  // ABOUT
  TITLE_TYPE_NOT_FOUND("해당 타입의 타이틀을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  // SEMINAR
  SEMINAR_TYPE_NOT_FOUND("존재하지 않는 세미나 타입입니다.", HttpStatus.NOT_FOUND),
  // BOOK
  BOOK_TYPE_NOT_FOUND("존재하지 않는 도서 타입입니다.", HttpStatus.NOT_FOUND),
  ;

  private final String message;
  private final HttpStatus httpStatus;

  ErrorCode(String message, HttpStatus httpStatus) {
    this.message = message;
    this.httpStatus = httpStatus;
  }
}
