package com.keeper.homepage.domain.attendance.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.attendance.entity.Attendance;
import com.keeper.homepage.domain.member.entity.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
      Member member = memberTestHelper.generate();
      attendanceTestHelper.builder()
          .date(LocalDate.of(2022, 1, 24))
          .member(member)
          .build();

      assertThatThrownBy(() -> attendanceTestHelper.builder()
          .date(LocalDate.of(2022, 1, 24))
          .member(member)
          .build())
          .isInstanceOf(Exception.class);
    }
  }

  @Nested
  @DisplayName("DB Default 테스트")
  class Default {

    @Test
    @DisplayName("default 값이 있는 컬럼은 null로 저장해도 저장에 성공한다.")
    void should_saveSuccessfully_when_defaultColumnIsNull() {
      Attendance attendanceBeforeSave = Attendance.builder()
          .ipAddress("0.0.0.0")
          .continuousDay(0)
          .member(memberTestHelper.generate())
          .build();
      LocalDateTime beforeSaveTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

      assertThat(attendanceBeforeSave.getTime()).isNull();
      assertThat(attendanceBeforeSave.getDate()).isNull();
      assertThat(attendanceBeforeSave.getPoint()).isNull();
      assertThat(attendanceBeforeSave.getRandomPoint()).isNull();
      assertThat(attendanceBeforeSave.getRankPoint()).isNull();
      assertThat(attendanceBeforeSave.getContinuousPoint()).isNull();

      Long attendanceId = attendanceRepository.save(attendanceBeforeSave).getId();
      em.flush();
      em.clear();
      Attendance savedAttendance = attendanceRepository.findById(attendanceId).orElseThrow();

      assertThat(LocalDateTime.of(
          savedAttendance.getDate(), savedAttendance.getTime().toLocalTime()))
          .isAfterOrEqualTo(beforeSaveTime)
          .isBeforeOrEqualTo(LocalDateTime.now());
      assertThat(savedAttendance.getPoint()).isEqualTo(100);
      assertThat(savedAttendance.getRandomPoint()).isEqualTo(0);
      assertThat(savedAttendance.getRankPoint()).isEqualTo(0);
      assertThat(savedAttendance.getContinuousPoint()).isEqualTo(0);
    }
  }
}
