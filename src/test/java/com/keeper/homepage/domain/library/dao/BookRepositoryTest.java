package com.keeper.homepage.domain.library.dao;

import static com.keeper.homepage.domain.library.entity.BookDepartment.BookDepartmentType.ETC;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.library.BookBorrowInfoTestHelper;
import com.keeper.homepage.domain.library.entity.Book;
import com.keeper.homepage.domain.library.entity.BookBorrowInfo;
import com.keeper.homepage.domain.library.entity.BookDepartment;
import com.keeper.homepage.domain.library.entity.BookDepartment.BookDepartmentType;
import com.keeper.homepage.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BookRepositoryTest extends IntegrationTest {

  private Book book;
  private Member member;
  private LocalDateTime expireDate;

  @BeforeEach
  void setUp() {
    book = bookTestHelper.generate();
    member = memberTestHelper.generate();
  }

  @Nested
  @DisplayName("Book Department DB 테스트")
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
          .map(BookDepartment::getType)
          .map(BookDepartmentType::getName)
          .collect(toList());
    }

    private String getName(BookDepartment bookDepartment) {
      return bookDepartment.getType().getName();
    }
  }

  @Nested
  @DisplayName("Book Remove 테스트")
  class BookRemoveTest {

    @Test
    @DisplayName("Book을 삭제하면 해당 Book의 BookBorrowInfo도 삭제되어야 한다.")
    void should_deleteBookBorrowInfo_when_deleteBook() {
      member.borrow(book, expireDate);
      em.flush();
      em.clear();

      book = bookRepository.findById(book.getId()).orElseThrow();
      bookRepository.delete(book);

      assertThat(bookBorrowInfoRepository.findAll()).hasSize(0);
      assertThat(bookBorrowInfoRepository.findAll()).doesNotContain(bookBorrowInfo);
    }
  }

  @Nested
  @DisplayName("DB NOT NULL DEFAULT 테스트")
  class NotNullDefaultTest {

    @Test
    @DisplayName("Book DB 디폴트 처리가 있는 값을 넣지 않았을 때 DB 디폴트 값으로 처리해야 한다.")
    void should_processDefault_when_EmptyBookValue() {
      em.flush();
      em.clear();

      Book findBook = bookRepository.findById(book.getId()).orElseThrow();

      assertThat(findBook.getBookDepartment().getId()).isEqualTo(ETC.getId());
      assertThat(findBook.getTotalQuantity()).isEqualTo(1);
      assertThat(findBook.getCurrentQuantity()).isEqualTo(1);
    }

    @Test
    @DisplayName("BookBorrowInfo DB 디폴트 처리가 있는 값을 넣지 않았을 때 DB 디폴트 값으로 처리해야 한다.")
    void should_processDefault_when_EmptyBookBorrowInfoValue() {
      BookBorrowInfo borrowInfo = bookBorrowInfoTestHelper.generate();
      em.flush();
      em.clear();

      BookBorrowInfo findBorrowInfo = bookBorrowInfoRepository.findById(borrowInfo.getId()).orElseThrow();

      assertThat(findBorrowInfo.getBorrowDate()).isBefore(LocalDateTime.now());
    }
  }
}
