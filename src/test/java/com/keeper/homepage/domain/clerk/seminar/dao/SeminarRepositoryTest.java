package com.keeper.homepage.domain.clerk.seminar.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.clerk.entity.seminar.Seminar;
import java.time.LocalDateTime;
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
    seminarId = seminar.getId();
  }

  @Nested
  @DisplayName("세미나 관리 테스트")
  class SeminarTest {

    @Test
    @DisplayName("DB에 세미나 등록을 성공해야 한다.")
    void should_success_when_createSeminar() {
      LocalDateTime now = LocalDateTime.now();
      Seminar seminarBuild = seminarTestHelper.builder()
          .openTime(now)
          .attendanceCloseTime(now.plusMinutes(3))
          .latenessCloseTime(now.plusMinutes(4))
          .attendanceCode("1234")
          .seminarName("세미나 제목입니다.")
          .build();

      Seminar savedSeminar = seminarRepository.save(seminarBuild);
      assertThat(savedSeminar.getOpenTime()).isEqualTo(seminarBuild.getOpenTime());
      assertThat(savedSeminar.getAttendanceCloseTime()).isEqualTo(seminarBuild.getAttendanceCloseTime());
      assertThat(savedSeminar.getLatenessCloseTime()).isEqualTo(seminarBuild.getLatenessCloseTime());
      assertThat(savedSeminar.getAttendanceCode()).isEqualTo(seminarBuild.getAttendanceCode());
      assertThat(savedSeminar.getName()).isEqualTo(seminarBuild.getName());
    }

    @Test
    @DisplayName("시간 값을 넣지 않았을 때 DB의 현재 시간으로 처리해야 한다.")
    void should_process_when_EmptyTime() {
      em.clear();
      LocalDateTime now = LocalDateTime.now();
      seminar = seminarRepository.findById(seminarId).orElseThrow();

      assertThat(seminar.getOpenTime()).isNotNull();
      assertThat(seminar.getRegisterTime()).isNotNull();
      assertThat(seminar.getUpdateTime()).isNotNull();
      assertThat(seminar.getOpenTime().isAfter(now.minusDays(2)));
      assertThat(seminar.getRegisterTime().isAfter(now.minusDays(2)));
      assertThat(seminar.getUpdateTime().isAfter(now.minusDays(2)));
    }

    @Test
    @DisplayName("DB에 등록된 세미나를 삭제해야 한다.")
    void should_success_when_deleteSeminar() {
      int beforeSeminarLength = seminarRepository.findAll().size();
      seminarRepository.delete(seminar);

      int afterSeminarLength = seminarRepository.findAll().size();
      assertThat(afterSeminarLength).isEqualTo(beforeSeminarLength - 1);
    }
  }
}
