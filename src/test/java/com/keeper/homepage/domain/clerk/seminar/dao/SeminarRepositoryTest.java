package com.keeper.homepage.domain.clerk.seminar.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.clerk.entity.seminar.Seminar;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SeminarRepositoryTest extends IntegrationTest {

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
          .build();

      Long seminarId = seminarRepository.save(seminarBuild).getId();
      Seminar savedSeminar = seminarRepository.findById(seminarId).orElseThrow();

      assertThat(savedSeminar.getOpenTime()).isEqualTo(seminarBuild.getOpenTime());
      assertThat(savedSeminar.getAttendanceCloseTime()).isEqualTo(seminarBuild.getAttendanceCloseTime());
      assertThat(savedSeminar.getLatenessCloseTime()).isEqualTo(seminarBuild.getLatenessCloseTime());
      assertThat(savedSeminar.getAttendanceCode()).isEqualTo(seminarBuild.getAttendanceCode());
      assertThat(savedSeminar.getName()).isEqualTo(seminarBuild.getName());
    }

    @Test
    @DisplayName("시간 값을 넣지 않았을 때 DB의 현재 시간으로 처리해야 한다.")
    void should_process_when_EmptyTime() {
      Long seminarId = seminarRepository.save(seminarTestHelper.generate()).getId();
      em.flush();
      em.clear();

      Seminar savedSeminar = seminarRepository.findById(seminarId).orElseThrow();

      assertThat(savedSeminar.getOpenTime()).isNotNull();
      assertThat(savedSeminar.getRegisterTime()).isNotNull();
      assertThat(savedSeminar.getUpdateTime()).isNotNull();
    }

    @Test
    @DisplayName("DB에 등록된 세미나를 삭제해야 한다.")
    void should_success_when_deleteSeminar() {
      Long seminarId = seminarRepository.save(seminarTestHelper.generate()).getId();
      int beforeSeminarLength = seminarRepository.findAll().size();
      Seminar savedSeminar = seminarRepository.findById(seminarId).orElseThrow();
      seminarRepository.delete(savedSeminar);

      int afterSeminarLength = seminarRepository.findAll().size();
      assertThat(afterSeminarLength).isEqualTo(beforeSeminarLength - 1);
    }
  }
}
