package com.keeper.homepage.domain.library.dao;

import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출대기중;
import static com.keeper.homepage.domain.library.entity.BookDepartment.BookDepartmentType.ETC;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.library.BookBorrowInfoTestHelper;
import com.keeper.homepage.domain.library.entity.Book;
import com.keeper.homepage.domain.library.entity.BookBorrowInfo;
import com.keeper.homepage.domain.library.entity.BookBorrowStatus;
import com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType;
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
      List<BookDepartment> bookDepartmentTypes = Arrays.stream(BookDepartmentType.values())
          .map(BookDepartment::getBookDepartmentBy)
          .toList();

      // when
      List<BookDepartment> bookDepartments = bookDepartmentRepository.findAll();

      // then
      assertThat(getIds(bookDepartments)).containsAll(getIds(bookDepartmentTypes));
      assertThat(getNames(bookDepartments)).containsAll(getNames(bookDepartmentTypes));
      assertThat(bookDepartments).hasSize(BookDepartmentType.values().length);
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

  @Nested
  @DisplayName("Book Borrow Status DB 테스트")
  class BookBorrowStatusRepositoryTest {

    @Test
    @DisplayName("BookBorrowStatusType Enum 에는 DB의 모든 BookBorrowStatusRepository 정보가 있어야 한다.")
    void should_allBookBorrowStatusInfoExist_when_givenBookBorrowStatusTypeEnum() {
      // given
      List<BookBorrowStatus> bookBorrowStatusesTypes = Arrays.stream(BookBorrowStatusType.values())
              .map(BookBorrowStatus::getBookBorrowStatusBy)
              .toList();

      // when
      List<BookBorrowStatus> bookBorrowStatuses = bookBorrowStatusRepository.findAll();

      // then
      assertThat(getIds(bookBorrowStatuses)).containsAll(getIds(bookBorrowStatusesTypes));
      assertThat(getStatuses(bookBorrowStatuses)).containsAll(getStatuses(bookBorrowStatusesTypes));
      assertThat(bookBorrowStatuses).hasSize(BookBorrowStatusType.values().length);
    }

    private List<Long> getIds(List<BookBorrowStatus> bookBorrowStatuses) {
      return bookBorrowStatuses.stream()
              .map(BookBorrowStatus::getId)
              .collect(toList());
    }

    private Long getId(BookBorrowStatus bookBorrowStatus) {
      return bookBorrowStatus.getId();
    }

    private List<String> getStatuses(List<BookBorrowStatus> bookBorrowStatuses) {
      return bookBorrowStatuses.stream()
              .map(BookBorrowStatus::getType)
              .map(BookBorrowStatusType::getStatus)
              .collect(toList());
    }

    private String getStatus(BookBorrowStatus bookBorrowStatus) {
      return bookBorrowStatus.getType().getStatus();
    }
  }
}
