package com.keeper.homepage.domain.library.dao;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.library.entity.BookDepartment;
import com.keeper.homepage.domain.library.entity.BookDepartment.BookDepartmentType;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BookRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("Book Department 테스트")
  class BookDepartmentTest {

    @Test
    @DisplayName("BookDepartmentType Enum 에는 DB의 모든 BookDepartment 정보가 있어야 한다.")
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

}
