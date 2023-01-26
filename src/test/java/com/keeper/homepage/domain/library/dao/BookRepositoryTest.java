package com.keeper.homepage.domain.library.dao;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.library.entity.Book;
import com.keeper.homepage.domain.library.entity.BookBorrowInfo;
import com.keeper.homepage.domain.library.entity.BookDepartment;
import com.keeper.homepage.domain.library.entity.BookDepartment.BookDepartmentType;
import com.keeper.homepage.domain.member.entity.Member;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BookRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("Book Department 테스트")
  class BookDepartmentTest {

    @Test
    @DisplayName("BookDepartmentType Enum 에는 DB의 모든 BookDepartment정보가 있어야 한다.")
    void should_allBookDepartmentInfoExist_when_givenBookDepartmentTypeEnum() {
      // given
      List<BookDepartment> bookDepartmentTypes = Arrays.stream(BookDepartmentType.values())
          .map(BookDepartment::getBookDepartmentBy)
          .toList();

      // when
      List<BookDepartment> bookDepartments = bookDepartmentRepository.findAll();

      // then
      assertThat(getIds(bookDepartments)).containsAll(getIds(bookDepartmentTypes));
      assertThat(getNames(bookDepartments)).containsAll(getNames(bookDepartmentTypes));
    }

    private List<Long> getIds(List<BookDepartment> bookDepartments) {
      return bookDepartments.stream()
          .map(BookDepartment::getId)
          .collect(toList());
    }

    private List<String> getNames(List<BookDepartment> bookDepartments) {
      return bookDepartments.stream()
          .map(BookDepartment::getName)
          .map(BookDepartmentType::getName)
          .collect(toList());
    }
  }

  @Nested
  @DisplayName("Book Borrow Info 테스트")
  class BookBorrowInfoTest {

    @Test
    @DisplayName("Book을 삭제하면 해당 Book의 BookBorrowInfo도 삭제되어야 한다.")
    void should_deleteBookBorrowInfo_when_deleteBook() {
      // given
      Book book = bookTestHelper.generateBook();
      Member member = memberTestHelper.builder()
          .loginId("ABC")
          .emailAddress("ABC@keeper.com")
          .password("password")
          .realName("realName")
          .nickname("nickname")
          .build();
      BookBorrowInfo bookBorrowInfo = bookTestHelper.generateBookBorrowInfo(book, member);
      book.addBookBorrowInfo(bookBorrowInfo);

      // when
      bookRepository.delete(book);
      List<BookBorrowInfo> allBookBorrowInfos = bookBorrowInfoRepository.findAll();

      // then
      assertThat(allBookBorrowInfos).isEmpty();
      assertThat(allBookBorrowInfos).doesNotContain(bookBorrowInfo);
    }
  }

}
