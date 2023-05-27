package com.keeper.homepage.domain.library.application;

import static com.keeper.homepage.domain.library.application.BookService.MAX_BORROWING_COUNT;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출승인;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.getBookBorrowStatusBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.library.entity.Book;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.error.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


public class BookServiceTest extends IntegrationTest {

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
  }
}
