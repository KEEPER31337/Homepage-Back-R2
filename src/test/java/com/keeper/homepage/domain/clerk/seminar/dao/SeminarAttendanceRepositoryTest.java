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
    @DisplayName("DB에 세미나 참석 정보를 저장해야 한다.")
    void should_success_when_attendSeminar() {
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
  @DisplayName("세미나 지각 사유 테스트")
  class SeminarAttendanceExcuseTest {

    @Test
    @DisplayName("DB에 세미나 지각 사유를 저장해야 한다.")
    void should_success_when_attendExcuseSeminar() {
      SeminarAttendance findAttendance = seminarAttendanceRepository.findById(seminarAttendanceId).orElseThrow();
      String excuse = "늦게 일어나서";
      SeminarAttendanceExcuse attendanceExcuseBuild = makeAttendanceExcuse(findAttendance, excuse);

      Long attendanceExcuseId = seminarAttendanceExcuseRepository.save(attendanceExcuseBuild).getId();
      em.flush();
      em.clear();

      SeminarAttendanceExcuse findAttendanceExcuse = seminarAttendanceExcuseRepository.findById(attendanceExcuseId)
          .orElseThrow();
      SeminarAttendance reFindAttendance = seminarAttendanceRepository.findById(seminarAttendanceId).orElseThrow();

      assertThat(findAttendanceExcuse.getAbsenceExcuse()).isEqualTo(
          attendanceExcuseBuild.getAbsenceExcuse());
      assertThat(reFindAttendance.getSeminarAttendanceExcuse()
          .getAbsenceExcuse()).isEqualTo(attendanceExcuseBuild.getAbsenceExcuse());
    }

    @Test
    @DisplayName("DB에 저장된 세미나 참석 정보를 삭제했을 때 지각 사유도 삭제되어야 한다.")
    void should_deleteSeminarAttendance_when_deleteSeminarAttendanceStatus() {
      SeminarAttendance findAttendance = seminarAttendanceRepository.findById(seminarAttendanceId).orElseThrow();
      String excuse = "늦게 일어나서";
      SeminarAttendanceExcuse attendanceExcuseBuild = makeAttendanceExcuse(findAttendance, excuse);

      seminarAttendanceExcuseRepository.save(attendanceExcuseBuild);
      em.flush();
      em.clear();

      int beforeAttendanceLength = seminarAttendanceRepository.findAll().size();
      int beforeAttendanceExcuseLength = seminarAttendanceExcuseRepository.findAll().size();
      SeminarAttendance reFindAttendance = seminarAttendanceRepository.findById(seminarAttendanceId).orElseThrow();
      seminarAttendanceRepository.delete(reFindAttendance);

      int afterAttendanceLength = seminarAttendanceRepository.findAll().size();
      int afterAttendanceExcuseLength = seminarAttendanceExcuseRepository.findAll().size();
      assertThat(afterAttendanceLength).isEqualTo(beforeAttendanceLength - 1);
      assertThat(afterAttendanceExcuseLength).isEqualTo(beforeAttendanceExcuseLength - 1);
    }

    private SeminarAttendanceExcuse makeAttendanceExcuse(SeminarAttendance attendance, String excuse) {
      return SeminarAttendanceExcuse.builder()
          .seminarAttendance(attendance)
          .absenceExcuse(excuse)
          .build();
    }
  }
}
