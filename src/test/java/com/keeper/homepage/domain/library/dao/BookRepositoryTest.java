package com.keeper.homepage.domain.library.dao;

import static com.keeper.homepage.domain.library.entity.BookDepartment.BookDepartmentType.CERTIFICATION;
import static com.keeper.homepage.domain.library.entity.BookDepartment.BookDepartmentType.DOCUMENT;
import static com.keeper.homepage.domain.library.entity.BookDepartment.BookDepartmentType.ETC;
import static com.keeper.homepage.domain.library.entity.BookDepartment.BookDepartmentType.LANGUAGE;
import static com.keeper.homepage.domain.library.entity.BookDepartment.BookDepartmentType.SECURITY;
import static com.keeper.homepage.domain.library.entity.BookDepartment.BookDepartmentType.TEXTBOOK;
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
      BookDepartmentType[] departmentTypes = BookDepartmentType.values();
      List<BookDepartment> bookDepartmentTypes = Arrays.stream(departmentTypes)
          .map(BookDepartment::getBookDepartmentBy)
          .toList();

      // when
      List<BookDepartment> bookDepartments = bookDepartmentRepository.findAll();

      // then
      assertThat(getIds(bookDepartments)).containsAll(getIds(bookDepartmentTypes));
      assertThat(getNames(bookDepartments)).containsAll(getNames(bookDepartmentTypes));
      assertThat(bookDepartments).hasSize(departmentTypes.length);
      for (int i = 0; i < departmentTypes.length; ++i) {
        assertThat(getId(bookDepartments.get(i))).isEqualTo(departmentTypes[i].getId());
        assertThat(getName(bookDepartments.get(i))).isEqualTo(departmentTypes[i].getName());
      }
    }

    private List<Long> getIds(List<BookDepartment> bookDepartments) {
      return bookDepartments.stream()
          .map(BookDepartment::getId)
          .collect(toList());
    }

    private Long getId(BookDepartment bookDepartment) {
      return bookDepartment.getId();
    }

    private List<String> getNames(List<BookDepartment> bookDepartments) {
      return bookDepartments.stream()
          .map(BookDepartment::getName)
          .map(BookDepartmentType::getName)
          .collect(toList());
    }

    private String getName(BookDepartment bookDepartment) {
      return bookDepartment.getName().getName();
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
      Member member = memberTestHelper.builder().build();
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
