package com.keeper.homepage.domain.clerk.seminar.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.clerk.entity.seminar.Seminar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SeminarRepositoryTest extends IntegrationTest {

  private Seminar seminar;
  private Long seminarId;

  @BeforeEach
  void setUp() {
    seminar = seminarTestHelper.generate();
    seminarId = seminarRepository.save(seminar).getId();
  }

  @Nested
  @DisplayName("세미나 관리 테스트")
  class SeminarTest {

    @Test
    @DisplayName("DB에 세미나 등록을 성공해야 한다.")
    void should_success_when_createSeminar() {
      Seminar savedSeminar = seminarRepository.findById(seminarId).orElseThrow();

      assertThat(savedSeminar.getOpenTime()).isEqualTo(seminar.getOpenTime());
      assertThat(savedSeminar.getAttendanceCloseTime()).isEqualTo(seminar.getAttendanceCloseTime());
      assertThat(savedSeminar.getLatenessCloseTime()).isEqualTo(seminar.getLatenessCloseTime());
      assertThat(savedSeminar.getAttendanceCode()).isEqualTo(seminar.getAttendanceCode());
      assertThat(savedSeminar.getName()).isEqualTo(seminar.getName());
    }

    @Test
    @DisplayName("DB에 등록된 세미나를 삭제해야 한다.")
    void should_success_when_deleteSeminar() {
      int beforeSeminarLength = seminarRepository.findAll().size();
      Seminar savedSeminar = seminarRepository.findById(seminarId).orElseThrow();
      seminarRepository.delete(savedSeminar);

      int afterSeminarLength = seminarRepository.findAll().size();
      assertThat(afterSeminarLength).isEqualTo(beforeSeminarLength - 1);
    }
  }
}
