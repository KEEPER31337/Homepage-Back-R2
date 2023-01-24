package com.keeper.homepage.domain.attendance.dao;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

class AttendanceRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("[is_duplicated] unique index test")
  class UniqueIndex {

    @Test
    @DisplayName("중복되지 않는 출석 정보는 저장에 성공해야 한다.")
    void should_success_when_notDuplicateAttendance() {
      attendanceTestHelper.builder()
          .date(LocalDate.of(2022, 1, 24))
          .build();

      Assertions.assertDoesNotThrow(() -> attendanceTestHelper.builder()
          .date(LocalDate.of(2022, 1, 25))
          .build());
    }

    @Test
    @DisplayName("중복되는 출석 정보는 오류를 반환해야 한다.")
    void should_throwException_when_duplicateAttendance() {
      Member member = memberTestHelper.builder().build();
      attendanceTestHelper.builder()
          .date(LocalDate.of(2022, 1, 24))
          .member(member)
          .build();

      assertThatThrownBy(() -> attendanceTestHelper.builder()
          .date(LocalDate.of(2022, 1, 24))
          .member(member)
          .build())
          .isInstanceOf(DataIntegrityViolationException.class);
    }
  }
}
