package com.keeper.homepage.domain.clerk.seminar.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.clerk.entity.seminar.SeminarAttendance;
import com.keeper.homepage.domain.clerk.entity.seminar.SeminarAttendanceExcuse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SeminarAttendanceRepositoryTest extends IntegrationTest {

  private SeminarAttendance seminarAttendance;
  private Long seminarAttendanceId;

  @BeforeEach
  void setUp() {
    seminarAttendance = seminarAttendanceTestHelper.generate();
    seminarAttendanceId = seminarAttendanceRepository.save(seminarAttendance).getId();
  }

  @Nested
  @DisplayName("세미나 참석 테스트")
  class SeminarAttendanceTest {

    @Test
    @DisplayName("DB에 저장된 세미나 참석 정보를 확인한다.")
    void should_check_when_SavedSeminarAttendance() {
      SeminarAttendance findAttendance = seminarAttendanceRepository.findById(seminarAttendanceId)
          .orElseThrow();

      assertThat(findAttendance.getMember().getId()).isEqualTo(seminarAttendance.getMember().getId());
      assertThat(findAttendance.getSeminar().getId()).isEqualTo(seminarAttendance.getSeminar().getId());
      assertThat(findAttendance.getSeminar().getName()).isEqualTo(seminarAttendance.getSeminar().getName());
      assertThat(findAttendance.getAttendTime()).isEqualTo(seminarAttendance.getAttendTime());
      assertThat(findAttendance.getSeminarAttendanceStatus()
          .getType()).isEqualTo(seminarAttendance.getSeminarAttendanceStatus()
          .getType());
    }
  }

  @Nested
  @DisplayName("세미나 지각 사유, 상태 테스트")
  class SeminarAttendanceExcuseAndStatusTest {

    @Test
    @DisplayName("DB에 세미나 지각 사유를 저장해야 한다.")
    void should_success_when_saveAttendExcuseSeminar() {
      SeminarAttendance findAttendance = seminarAttendanceRepository.findById(seminarAttendanceId).orElseThrow();
      String excuse = "늦게 일어나서";
      findAttendance.setLatenessStatus(excuse);

      em.flush();
      em.clear();

      SeminarAttendanceExcuse findAttendanceExcuse = seminarAttendanceExcuseRepository.findById(seminarAttendanceId)
          .orElseThrow();

      SeminarAttendance reFindAttendance = seminarAttendanceRepository.findById(seminarAttendanceId).orElseThrow();
      assertThat(findAttendanceExcuse.getAbsenceExcuse()).isEqualTo(excuse);
      assertThat(reFindAttendance.getExcuse().get()).isEqualTo(excuse);
    }
  }

  @Nested
  @DisplayName("세미나 참석 삭제 테스트")
  class SeminarAttendanceDeleteTest {
    @Test
    @DisplayName("DB에 저장된 세미나 참석 정보를 삭제했을 때 지각 사유와 상태 정보도 삭제되어야 한다.")
    void should_deleteSeminarAttendance_when_deleteSeminarAttendanceStatus() {
      SeminarAttendance findAttendance = seminarAttendanceRepository.findById(seminarAttendanceId).orElseThrow();
      String excuse = "늦게 일어나서";
      findAttendance.setLatenessStatus(excuse);

      em.flush();
      em.clear();

      int beforeAttendanceExcuseLength = seminarAttendanceExcuseRepository.findAll().size();
      int beforeAttendanceStatusLength = seminarAttendanceStatusRepository.findAll().size();
      int beforeAttendanceLength = seminarAttendanceRepository.findAll().size();
      SeminarAttendance reFindAttendance = seminarAttendanceRepository.findById(seminarAttendanceId).orElseThrow();
      seminarAttendanceRepository.delete(reFindAttendance);

      int afterAttendanceExcuseLength = seminarAttendanceExcuseRepository.findAll().size();
      int afterAttendanceStatusLength = seminarAttendanceStatusRepository.findAll().size();
      int afterAttendanceLength = seminarAttendanceRepository.findAll().size();
      assertThat(afterAttendanceExcuseLength).isEqualTo(beforeAttendanceExcuseLength - 1);
      assertThat(afterAttendanceStatusLength).isEqualTo(beforeAttendanceStatusLength - 1);
      assertThat(afterAttendanceLength).isEqualTo(beforeAttendanceLength - 1);
    }
  }
}
