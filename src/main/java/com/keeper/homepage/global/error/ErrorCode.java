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
  MEMBER_JOB_NOT_FOUND("해당 직책을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  MEMBER_JOB_IS_NOT_EXECUTIVE("해당 직책은 임원 직책이 아닙니다.", HttpStatus.BAD_REQUEST),
  MEMBER_CANNOT_FOLLOW_ME("스스로를 팔로우 할 수 없습니다.", HttpStatus.BAD_REQUEST),
  // ABOUT
  TITLE_TYPE_NOT_FOUND("해당 타입의 타이틀을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  // SEMINAR
  SEMINAR_NOT_FOUND("해당 세미나를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  SEMINAR_TIME_NOT_AVAILABLE("올바르지 않은 시간 값입니다", HttpStatus.BAD_REQUEST),
  SEMINAR_ATTENDANCE_CODE_NOT_AVAILABLE("올바르지 않은 출석 코드입니다.", HttpStatus.BAD_REQUEST),
  SEMINAR_ATTENDANCE_ATTEMPT_NOT_AVAILABLE("출석코드 입력 가능 횟수를 초과하여 출석이 불가능합니다.", HttpStatus.BAD_REQUEST),
  SEMINAR_ATTENDANCE_DUPLICATE("이미 출석을 완료한 계정입니다.", HttpStatus.CONFLICT),
  SEMINAR_ATTENDANCE_UNABLE("활동회원이 아닌 회원은 출석이 불가합니다.", HttpStatus.BAD_REQUEST),
  // POST
  POST_CATEGORY_NOT_FOUND("존재하지 않는 게시글 카테고리입니다.", HttpStatus.NOT_FOUND),
  POST_NOT_FOUND("존재하지 않는 게시글입니다.", HttpStatus.NOT_FOUND),
  POST_INACCESSIBLE("게시글에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),
  POST_ACCESS_CONDITION_NEED("게시글 열람 조건을 충족하지 않습니다.", HttpStatus.BAD_REQUEST),
  POST_PASSWORD_MISMATCH("게시글의 비밀번호가 일치하지 않습니다.", HttpStatus.FORBIDDEN),
  POST_PASSWORD_NEED("게시글의 비밀번호를 입력해주세요.", HttpStatus.BAD_REQUEST),
  POST_CONTENT_NEED("게시글의 내용을 입력해주세요.", HttpStatus.BAD_REQUEST),
  POST_SEARCH_TYPE_NOT_FOUND("존재하지 않는 검색 타입입니다.", HttpStatus.BAD_REQUEST),
  POST_COMMENT_NEED("게시글에 댓글 작성이 필요합니다.", HttpStatus.BAD_REQUEST),
  POST_HAS_NOT_THAT_FILE("해당 파일은 해당 게시글의 파일이 아닙니다.", HttpStatus.BAD_REQUEST),
  // COMMENT
  COMMENT_NOT_FOUND("존재하지 않는 댓글입니다.", HttpStatus.NOT_FOUND),
  COMMENT_NOT_WRITER("댓글 작성자가 아닙니다.", HttpStatus.BAD_REQUEST),
  COMMENT_IS_NOT_PARENT("대댓글에는 대댓글을 달 수 없습니다.", HttpStatus.BAD_REQUEST),
  COMMENT_NOT_ALLOWED("댓글 작성이 허용되지 않습니다.", HttpStatus.BAD_REQUEST),
  // BOOK
  BOOK_NOT_FOUND("존재하지 않는 도서입니다.", HttpStatus.NOT_FOUND),
  BOOK_DELETE_FAILED_IN_BORROWING("누군가가 책을 빌리고 있어 책을 삭제할 수 없습니다.", HttpStatus.BAD_REQUEST),
  BOOK_CANNOT_BORROW("책을 빌릴 수 없습니다.", HttpStatus.BAD_REQUEST),
  BOOK_CANNOT_RETURN_EXCEED_TOTAL_QUANTITY("전체 수량을 초과하여 책을 반납할 수 없습니다.", HttpStatus.BAD_REQUEST),
  BOOK_CANNOT_UPDATE_EXCEED_CURRENT_QUANTITY("현재 대여중인 수량보다 적은 수량으로 변경할 수 없습니다.", HttpStatus.BAD_REQUEST),
  BOOK_SEARCH_TYPE_NOT_FOUND("올바르지 않은 검색 타입입니다.", HttpStatus.BAD_REQUEST),
  BOOK_BORROWING_COUNT_OVER("대출 신청 가능 수량을 초과했습니다.", HttpStatus.BAD_REQUEST),
  BOOK_CURRENT_QUANTITY_IS_ZERO("현재 수량이 없는 책은 대출 신청이 불가합니다.", HttpStatus.BAD_REQUEST),
  BORROW_REQUEST_ALREADY("이미 대출 신청을 한 책은 대출 신청이 불가합니다.", HttpStatus.BAD_REQUEST),
  BORROW_NOT_FOUND("존재하지 않는 대출 기록입니다.", HttpStatus.NOT_FOUND),
  BORROW_STATUS_IS_NOT_REQUESTS("대출 대기중이 아닙니다.", HttpStatus.BAD_REQUEST),
  BORROW_STATUS_IS_NOT_WAITING_RETURN("반납 대기중이 아닙니다.", HttpStatus.BAD_REQUEST),
  BORROW_STATUS_IS_NOT_BORROW_APPROVAL("대출 승인된 도서가 아닙니다.", HttpStatus.BAD_REQUEST),
  BORROW_REQUEST_RETURN_DENY("대출자가 본인이 아니므로 반납 신청이 불가합니다.", HttpStatus.BAD_REQUEST),
  // STUDY
  STUDY_NOT_FOUND("존재하지 않는 스터디입니다.", HttpStatus.NOT_FOUND),
  STUDY_INACCESSIBLE("스터디장만 접근할 수 있습니다.", HttpStatus.BAD_REQUEST),
  STUDY_LINK_NEED("스터디 링크는 하나 이상 필수입니다.", HttpStatus.BAD_REQUEST),
  STUDY_HEAD_MEMBER_CANNOT_LEAVE("스터디장은 스터디 탈퇴 할 수 없습니다.", HttpStatus.BAD_REQUEST),
  // GAME
  IS_ALREADY_PLAYED("이미 게임 플레이 가능 횟수만큼 플레이 하였습니다.", HttpStatus.BAD_REQUEST),
  NOT_ENOUGH_POINT("베팅 포인트는 보유한 포인트보다 많을 수 없습니다.", HttpStatus.BAD_REQUEST),
  POINT_MUST_BE_POSITIVE("베팅 포인트는 양수여야 합니다.", HttpStatus.BAD_REQUEST),
  NOT_PLAYED_YET("아직 게임을 시작하지 않았습니다.", HttpStatus.BAD_REQUEST),
  // FILE
  FILE_NOT_FOUND("해당 파일은 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
  // ATTENDANCE
  ATTENDANCE_ALREADY("이미 출석을 완료했습니다.", HttpStatus.BAD_REQUEST),
  ATTENDANCE_NOT_FOUND("출석 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  // CTF
  CTF_CONTEST_NOT_FOUND("해당 대회는 끝났거나 없는 대회입니다.", HttpStatus.BAD_REQUEST),
  CTF_TEAM_ALREADY_JOIN("이미 가입한 팀이 있어 팀 가입이 불가합니다.", HttpStatus.BAD_REQUEST),
  CTF_TEAM_NOT_FOUND("해당 팀을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
  CTF_TEAM_INACCESSIBLE("해당 팀에 접근이 불가합니다.", HttpStatus.BAD_REQUEST),
  // MERIT
  MERIT_TYPE_NOT_FOUND("해당 상벌점 타입을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  MERIT_TYPE_DETAIL_DUPLICATE("해당 상벌점 타입의 사유가 존재합니다.", HttpStatus.CONFLICT),
  // ELECTION
  ELECTION_NOT_FOUND("존재하지 않는 선거입니다.", HttpStatus.NOT_FOUND),
  ELECTION_CANNOT_DELETE("비공개 상태 선거만 삭제 가능합니다.", HttpStatus.BAD_REQUEST),
  ELECTION_CANDIDATE_NOT_FOUND("해당 후보자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  ELECTION_VOTER_NOT_FOUND("해당 투표자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  ELECTION_CANDIDATE_CANNOT_REGISTER("해당 직위는 후보자 등록 불가합니다.", HttpStatus.BAD_REQUEST),
  ELECTION_CANDIDATE_CANNOT_DELETE("비공개 상태 선거에서만 후보자 삭제가 가능합니다.", HttpStatus.BAD_REQUEST),
  ELECTION_VOTER_CANNOT_DELETE("비공개 상태 선거에서만 투표자 삭제가 가능합니다.", HttpStatus.BAD_REQUEST),
  ;

  private final String message;
  private final HttpStatus httpStatus;

  ErrorCode(String message, HttpStatus httpStatus) {
    this.message = message;
    this.httpStatus = httpStatus;
  }
}
