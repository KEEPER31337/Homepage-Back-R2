package com.keeper.homepage.domain.seminar.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import java.time.LocalDate;
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
      LocalDateTime now = LocalDateTime.now().withNano(0);
      Seminar seminarBuild = seminarTestHelper.builder()
          .openTime(now)
          .attendanceCloseTime(now.plusMinutes(3))
          .latenessCloseTime(now.plusMinutes(4))
          .seminarName("세미나 제목입니다.")
          .build();

      Long seminarId = seminarBuild.getId();
      em.clear();

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
      Long seminarId = seminarTestHelper.generate().getId();
      em.clear();

      LocalDateTime now = LocalDateTime.now();
      Seminar savedSeminar = seminarRepository.findById(seminarId).orElseThrow();

      assertThat(savedSeminar.getOpenTime()).isAfter(now.minusDays(2));
      assertThat(savedSeminar.getRegisterTime()).isAfter(now.minusDays(2));
      assertThat(savedSeminar.getUpdateTime()).isAfter(now.minusDays(2));
    }
    
    @Test
    @DisplayName("세미나 이름을 넣지 않았을 때 TRIGGER가 동작해야 한다.")
    void should_success_when_EmptySeminarName() {
      Long seminarId = seminarTestHelper.generate().getId();
      em.clear();

      Seminar savedSeminar = seminarRepository.findById(seminarId).orElseThrow();
      assertThat(savedSeminar.getName()).isNotNull();
    }

    @Test
    @DisplayName("DB에 등록된 세미나를 삭제해야 한다.")
    void should_success_when_deleteSeminar() {
      Seminar seminar = seminarTestHelper.generate();
      int beforeSeminarLength = seminarRepository.findAll().size();
      seminarRepository.delete(seminar);

      int afterSeminarLength = seminarRepository.findAll().size();
      assertThat(afterSeminarLength).isEqualTo(beforeSeminarLength - 1);
    }
  }
  
  @Nested
  @DisplayName("세미나 조회 테스트")
  class SeminarSearchTest {

    @Test
    @DisplayName("세미나를 날짜로 필터링하여 조회한다.")
    public void should_filterDate_when_searchSeminar() throws Exception {
      Seminar seminar = seminarTestHelper.generate();
      em.clear();

      LocalDate dateNow = LocalDate.now();
      Seminar findSeminar = seminarRepository.findByOpenTime(dateNow);

      assertThat(findSeminar.getOpenTime().toLocalDate()).isEqualTo(dateNow);
    }
  }
}
