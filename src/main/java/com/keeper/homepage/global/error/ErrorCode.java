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
  SEMINAR_NOT_FOUND("해당 세미나를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  SEMINAR_TIME_NOT_AVAILABLE("올바르지 않은 시간 값입니다", HttpStatus.BAD_REQUEST),
  SEMINAR_ATTENDANCE_CODE_NOT_AVAILABLE("올바르지 않은 출석 코드입니다.", HttpStatus.BAD_REQUEST),
  SEMINAR_ATTENDANCE_DUPLICATE("이미 출석을 완료한 계정입니다.", HttpStatus.CONFLICT),
  // POST
  POST_CATEGORY_NOT_FOUND("존재하지 않는 게시글 카테고리입니다.", HttpStatus.NOT_FOUND),
  POST_NOT_FOUND("존재하지 않는 게시글입니다.", HttpStatus.NOT_FOUND),
  POST_CANNOT_ACCESSIBLE("게시글에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),
  POST_PASSWORD_MISMATCH("게시글의 비밀번호가 일치하지 않습니다.", HttpStatus.FORBIDDEN),
  POST_PASSWORD_NEED("게시글의 비밀번호를 입력해주세요.", HttpStatus.BAD_REQUEST),
  // COMMENT
  COMMENT_NOT_FOUND("존재하지 않는 댓글입니다.", HttpStatus.NOT_FOUND),
  COMMENT_NOT_WRITER("댓글 작성자가 아닙니다.", HttpStatus.BAD_REQUEST),
  COMMENT_NOT_PARENT("대댓글에는 대댓글을 달 수 없습니다.", HttpStatus.BAD_REQUEST),
  // BOOK
  BOOK_NOT_FOUND("존재하지 않는 도서입니다.", HttpStatus.NOT_FOUND),
  BOOK_TYPE_NOT_FOUND("존재하지 않는 도서 타입입니다.", HttpStatus.NOT_FOUND),
  BOOK_DELETE_FAILED_IN_BORROWING("누군가가 책을 빌리고 있어 책을 삭제할 수 없습니다.", HttpStatus.BAD_REQUEST),
  BOOK_CANNOT_BORROW("책을 빌릴 수 없습니다.", HttpStatus.BAD_REQUEST),
  BOOK_CANNOT_RETURN_EXCEED_TOTAL_QUANTITY("전체 수량을 초과하여 책을 반납할 수 없습니다.", HttpStatus.BAD_REQUEST),
  BOOK_CANNOT_UPDATE_EXCEED_CURRENT_QUANTITY("현재 대여중인 수량보다 적은 수량으로 변경할 수 없습니다.", HttpStatus.BAD_REQUEST),
  BORROW_NOT_FOUND("존재하지 않는 대출 기록입니다.", HttpStatus.NOT_FOUND),
  BORROW_STATUS_IS_NOT_REQUESTS("대출 대기중이 아닙니다.", HttpStatus.BAD_REQUEST),
  BORROW_STATUS_IS_NOT_WAITING_RETURN("반납 대기중이 아닙니다.", HttpStatus.BAD_REQUEST),
  // SURVEY
  SURVEY_REPLY_TYPE_NOT_FOUND("존재하지 않는 응답 종류입니다.", HttpStatus.NOT_FOUND),
  ;

  private final String message;
  private final HttpStatus httpStatus;

  ErrorCode(String message, HttpStatus httpStatus) {
    this.message = message;
    this.httpStatus = httpStatus;
  }
}
