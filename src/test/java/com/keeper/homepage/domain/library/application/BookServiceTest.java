package com.keeper.homepage.domain.library.application;

import static com.keeper.homepage.domain.library.application.BookService.MAX_BORROWING_COUNT;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출반려;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출대기;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출중;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.반납대기;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.getBookBorrowStatusBy;
import static com.keeper.homepage.global.error.ErrorCode.BORROW_STATUS_IS_NOT_BORROW_WAIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.library.dto.req.BookSearchType;
import com.keeper.homepage.domain.library.dto.resp.BookResponse;
import com.keeper.homepage.domain.library.entity.Book;
import com.keeper.homepage.domain.library.entity.BookBorrowInfo;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.error.BusinessException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public class BookServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("도서 목록 조회")
  class GetBooks {

    private Member member;
    private Book book;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.generate();
      book = bookTestHelper.generate();
    }

    @Test
    @DisplayName("현재수량이 0이 아니고, 회원이 빌린 책의 개수가 5권 미만이면 도서 대여 가능 응답을 해야 한다.")
    public void 현재수량이_0이_아니고_회원이_빌린_책의_개수가_5권_미만이면_도서_대여_가능_응답을_해야_한다() {
      Page<BookResponse> books = bookService.getBooks(member, BookSearchType.ALL, "", PageRequest.of(0, 5));

      assertThat(books.getContent().get(0).isCanBorrow()).isTrue();
    }

    @Test
    @DisplayName("현재수량이 0이라면 도서 대여 불가능 응답을 해야 한다.")
    public void 현재수량이_0이라면_도서_대여_불가능_응답을_해야_한다() {
      bookBorrowInfoTestHelper.builder().book(book).borrowStatus(getBookBorrowStatusBy(대출중))
          .build();

      Page<BookResponse> books = bookService.getBooks(member, BookSearchType.ALL, "", PageRequest.of(0, 5));

      assertThat(books.getContent().get(0).isCanBorrow()).isFalse();
    }

    @Test
    @DisplayName("회원이 빌린 책의 개수가 5권이라면 도서 대여 불가능 응답을 해야 한다.")
    public void 회원이_빌린_책의_개수가_5권이라면_도서_대여_불가능_응답을_해야_한다() {
      for (int i = 0; i < 5; i++) {
        bookBorrowInfoTestHelper.builder()
            .member(member)
            .borrowStatus(getBookBorrowStatusBy(대출중))
            .build();
      }
      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).get();

      Page<BookResponse> books = bookService.getBooks(member, BookSearchType.ALL, "", PageRequest.of(0, 5));

      assertThat(books.getContent().get(0).isCanBorrow()).isFalse();
    }

    @Test
    @DisplayName("회원이 빌린 책의 상태가 대출 대기, 대출 중, 반납 대기일 경우 도서 대여 불가능 응답을 해야 한다.")
    public void 회원이_빌린_책의_상태가_대출_대기_대출_중_반납_대기일_경우_도서_대여_불가능_응답을_해야_한다() throws Exception {
      bookBorrowInfoTestHelper.builder()
          .member(member)
          .book(book)
          .borrowStatus(getBookBorrowStatusBy(대출대기))
          .build();
      bookBorrowInfoTestHelper.builder()
          .member(member)
          .borrowStatus(getBookBorrowStatusBy(대출중))
          .build();
      bookBorrowInfoTestHelper.builder()
          .member(member)
          .borrowStatus(getBookBorrowStatusBy(반납대기))
          .build();
      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).get();

      Page<BookResponse> books = bookService.getBooks(member, BookSearchType.ALL, "", PageRequest.of(0, 5));

      assertThat(books.getContent().get(0).isCanBorrow()).isFalse();
      assertThat(books.getContent().get(1).isCanBorrow()).isFalse();
      assertThat(books.getContent().get(2).isCanBorrow()).isFalse();
    }
  }

  @Nested
  @DisplayName("도서 대출 신청")
  class RequestBookBorrow {

    private Member member;
    private Book book;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.generate();
      book = bookTestHelper.generate();
    }

    @Test
    @DisplayName("도서 대출중인 권수가 5권 이하면 도서 대출은 성공해야 한다.")
    public void 도서_대출중인_권수가_5권_이하면_도서_대출은_성공해야_한다() {
      assertDoesNotThrow(() -> bookService.requestBorrow(member, book.getId()));
      assertThat(bookBorrowInfoRepository.findByMemberAndBook(member, book)).isNotEmpty();
    }

    @Test
    @DisplayName("도서 대출중인 권수가 5권 초과면 도서 대출은 실패해야 한다.")
    public void 도서_대출중인_권수가_5권_초과면_도서_대출은_실패해야_한다() {
      for (int i = 0; i < 4; i++) {
        bookBorrowInfoTestHelper.builder()
            .member(member)
            .book(book)
            .borrowStatus(getBookBorrowStatusBy(대출중))
            .build();
      }
      bookBorrowInfoTestHelper.builder()
          .member(member)
          .book(book)
          .borrowStatus(getBookBorrowStatusBy(대출대기))
          .build();

      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).get();

      assertThat(member.getCountWaitOrInBorrowing()).isEqualTo(MAX_BORROWING_COUNT);
      assertThrows(BusinessException.class, () -> bookService.requestBorrow(member, book.getId()));
    }

    @Test
    @DisplayName("현재 수량이 없는 책은 도서 대출이 실패해야 한다.")
    public void 현재_수량이_없는_책은_도서_대출이_실패해야_한다() {
      bookBorrowInfoTestHelper.builder()
          .member(member)
          .book(book)
          .borrowStatus(getBookBorrowStatusBy(대출중))
          .build();

      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).get();

      assertThrows(BusinessException.class, () -> bookService.requestBorrow(member, book.getId()));
    }

    @Test
    @DisplayName("이미 신청한 책은 도서 대출이 실패해야 한다.")
    public void 이미_신청한_책은_도서_대출이_실패해야_한다() {
      bookBorrowInfoTestHelper.builder()
          .member(member)
          .book(book)
          .borrowStatus(getBookBorrowStatusBy(대출대기))
          .build();

      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).get();

      assertThrows(BusinessException.class, () -> bookService.requestBorrow(member, book.getId()));
    }
  }

  @Nested
  @DisplayName("도서 대출 신청 취소")
  class CancelRequestBookBorrow {

    private Member member;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.generate();
    }

    @Test
    @DisplayName("도서 대출 신청 취소는 성공해야 한다.")
    public void 도서_대출_신청_취소는_성공해야_한다() throws Exception {
      //given
      long bookBorrowInfoId = bookBorrowInfoTestHelper.builder()
          .member(member)
          .borrowStatus(getBookBorrowStatusBy(대출대기))
          .build()
          .getId();

      //when
      bookService.cancelBorrow(member, bookBorrowInfoId);

      em.flush();
      em.clear();
      //then
      assertThat(bookBorrowInfoRepository.findById(bookBorrowInfoId)).isEmpty();
    }

    @Test
    @DisplayName("대출 대기중 상태가 아닌 도서는 도서 대출 신청 취소가 실패한다.")
    public void 대출_대기중_상태가_아닌_도서는_도서_대출_신청_취소가_실패한다() throws Exception {
      long bookBorrowInfoId = bookBorrowInfoTestHelper.builder()
          .member(member)
          .borrowStatus(getBookBorrowStatusBy(대출중))
          .build()
          .getId();

      assertThrows(BusinessException.class, () -> {
        bookService.cancelBorrow(member, bookBorrowInfoId);
      }, BORROW_STATUS_IS_NOT_BORROW_WAIT.getMessage());
    }
  }

  @Nested
  @DisplayName("도서 반납 요청")
  class requestReturn {

    private Member member;
    private Book book;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.generate();
      book = bookTestHelper.generate();
    }

    @Test
    @DisplayName("도서 반납 요청 시 lastRequestDate가 갱신되어야 한다.")
    public void 도서_반납_요청_시_lastRequestDate가_갱신되어야_한다() {
      BookBorrowInfo bookBorrowInfo = bookBorrowInfoTestHelper.builder()
          .member(member)
          .book(book)
          .borrowStatus(getBookBorrowStatusBy(대출중))
          .build();

      long borrowId = bookBorrowInfo.getId();

      LocalDateTime beforeRequestDate = LocalDateTime.now();
      bookService.requestReturn(member, borrowId);
      LocalDateTime afterRequestDate = LocalDateTime.now();
      BookBorrowInfo updateInfo = bookBorrowInfoRepository.findById(borrowId).orElseThrow();

      assertThat(updateInfo.getLastRequestDate()).isAfter(beforeRequestDate);
      assertThat(updateInfo.getLastRequestDate()).isBefore(afterRequestDate);
    }

    @Test
    @DisplayName("도서 반납 요청 시 도서 상태가 갱신되어야 한다.")
    public void 도서_반납_요청_시_도서_상태가_갱신되어야_한다() {
      BookBorrowInfo bookBorrowInfo = bookBorrowInfoTestHelper.builder()
          .member(member)
          .book(book)
          .borrowStatus(getBookBorrowStatusBy(대출중))
          .build();

      long borrowId = bookBorrowInfo.getId();

      bookService.requestReturn(member, borrowId);
      BookBorrowInfo borrowInfo = bookBorrowInfoRepository.findById(borrowId).orElseThrow();

      assertThat(borrowInfo.getBorrowStatus().getType()).isEqualTo(반납대기);
    }

    @Test
    @DisplayName("도서가 대출 승인 상태가 아니라면 도서 반납 요청은 실패해야 한다.")
    public void 도서가_대출_승인_상태가_아니라면_도서_반납_요청은_실패해야_한다() {
      BookBorrowInfo bookBorrowInfo = bookBorrowInfoTestHelper.builder()
          .member(member)
          .book(book)
          .borrowStatus(getBookBorrowStatusBy(대출반려))
          .build();

      long borrowId = bookBorrowInfo.getId();

      assertThrows(BusinessException.class, () -> bookService.requestReturn(member, borrowId));
    }

    @Test
    @DisplayName("대출 내역이 없다면 도서 반납 요청이 실패해야 한다.")
    public void 대출_내역이_없다면_도서_반납_요청이_실패해야_한다() {
      long notExistBorrowId = 5;

      assertThrows(BusinessException.class, () -> bookService.requestReturn(member, notExistBorrowId));
    }

    @Test
    @DisplayName("본인이 아니라면 도서 반납 요청이 실패해야 한다.")
    public void 본인이_아니라면_도서_반납_요청이_실패해야_한다() {
      Member otherMember = memberTestHelper.generate();
      BookBorrowInfo bookBorrowInfo = bookBorrowInfoTestHelper.builder()
          .member(otherMember)
          .book(book)
          .borrowStatus(getBookBorrowStatusBy(대출중))
          .build();

      long borrowId = bookBorrowInfo.getId();

      assertThrows(BusinessException.class, () -> bookService.requestReturn(member, borrowId));
    }
  }
}
