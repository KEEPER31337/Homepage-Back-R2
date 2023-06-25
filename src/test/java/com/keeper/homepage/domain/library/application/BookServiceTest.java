package com.keeper.homepage.domain.library.application;

import static com.keeper.homepage.domain.library.application.BookService.MAX_BORROWING_COUNT;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출대기중;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출승인;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.getBookBorrowStatusBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.library.dto.resp.BookResponse;
import com.keeper.homepage.domain.library.entity.Book;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.error.BusinessException;
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
    public void 현재수량이_0이_아니고_회원이_빌린_책의_개수가_5권_미만이면_도서_대여_가능_응답을_해야_한다() throws Exception {
      Page<BookResponse> books = bookService.getBooks(member, null, null, PageRequest.of(0, 5));

      assertThat(books.getContent().get(0).isCanBorrow()).isTrue();
    }

    @Test
    @DisplayName("현재수량이 0이라면 도서 대여 불가능 응답을 해야 한다.")
    public void 현재수량이_0이라면_도서_대여_불가능_응답을_해야_한다() throws Exception {
      bookBorrowInfoTestHelper.builder().book(book).borrowStatus(getBookBorrowStatusBy(대출승인)).build();

      Page<BookResponse> books = bookService.getBooks(member, null, null, PageRequest.of(0, 5));

      assertThat(books.getContent().get(0).isCanBorrow()).isFalse();
    }

    @Test
    @DisplayName("회원이 빌린 책의 개수가 5권이라면 도서 대여 불가능 응답을 해야 한다.")
    public void 회원이_빌린_책의_개수가_5권이라면_도서_대여_불가능_응답을_해야_한다() throws Exception {
      for (int i = 0; i < 5; i++) {
        bookBorrowInfoTestHelper.builder()
            .member(member)
            .book(book)
            .borrowStatus(getBookBorrowStatusBy(대출승인))
            .build();
      }
      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).get();

      Page<BookResponse> books = bookService.getBooks(member, null, null, PageRequest.of(0, 5));

      assertThat(books.getContent().get(0).isCanBorrow()).isFalse();
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
    public void 도서_대출중인_권수가_5권_이하면_도서_대출은_성공해야_한다() throws Exception {
      assertDoesNotThrow(() -> bookService.requestBorrow(member, book.getId()));
      assertThat(bookBorrowInfoRepository.findByMemberAndBook(member, book)).isNotEmpty();
    }

    @Test
    @DisplayName("도서 대출중인 권수가 5권 초과면 도서 대출은 실패해야 한다.")
    public void 도서_대출중인_권수가_5권_초과면_도서_대출은_실패해야_한다() throws Exception {
      for (int i = 0; i < 5; i++) {
        bookBorrowInfoTestHelper.builder()
            .member(member)
            .book(book)
            .borrowStatus(getBookBorrowStatusBy(대출승인))
            .build();
      }
      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).get();

      assertThat(member.getCountInBorrowing()).isEqualTo(MAX_BORROWING_COUNT);
      assertThrows(BusinessException.class, () -> bookService.requestBorrow(member, book.getId()));
    }

    @Test
    @DisplayName("현재 수량이 없는 책은 도서 대출이 실패해야 한다.")
    public void 현재_수량이_없는_책은_도서_대출이_실패해야_한다() throws Exception {
      bookBorrowInfoTestHelper.builder()
          .member(member)
          .book(book)
          .borrowStatus(getBookBorrowStatusBy(대출승인))
          .build();

      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).get();

      assertThrows(BusinessException.class, () -> bookService.requestBorrow(member, book.getId()));
    }

    @Test
    @DisplayName("이미 신청한 책은 도서 대출이 실패해야 한다.")
    public void 이미_신청한_책은_도서_대출이_실패해야_한다() throws Exception {
      bookBorrowInfoTestHelper.builder()
          .member(member)
          .book(book)
          .borrowStatus(getBookBorrowStatusBy(대출대기중))
          .build();

      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).get();

      assertThrows(BusinessException.class, () -> bookService.requestBorrow(member, book.getId()));
    }
  }
}
