package com.keeper.homepage.domain.clerk.seminar.dao;

import static com.keeper.homepage.domain.clerk.entity.seminar.SeminarAttendanceStatus.SeminarAttendanceStatusType.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.clerk.entity.seminar.Seminar;
import com.keeper.homepage.domain.clerk.entity.seminar.SeminarAttendance;
import com.keeper.homepage.domain.clerk.entity.seminar.SeminarAttendanceExcuse;
import com.keeper.homepage.domain.clerk.entity.seminar.SeminarAttendanceStatus;
import com.keeper.homepage.domain.clerk.entity.seminar.SeminarAttendanceStatus.SeminarAttendanceStatusType;
import com.keeper.homepage.domain.member.entity.Member;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SeminarAttendanceRepositoryTest extends IntegrationTest {

  private Seminar seminar;
  private Member member;
  private SeminarAttendance seminarAttendance;
  private SeminarAttendanceStatusType statusType;
  private Long seminarAttendanceId;

  @BeforeEach
  void setUp() {
    seminar = seminarTestHelper.generate();
    member = memberTestHelper.generate();
    statusType = ATTENDANCE;
    seminarAttendance = seminarAttendanceRepository.save(SeminarAttendance.builder()
        .seminar(seminar)
        .member(member)
        .seminarAttendanceStatus(initAttendanceStatus(statusType))
        .build());
    seminarAttendanceId = seminarAttendance.getId();
  }

  private SeminarAttendanceStatus initAttendanceStatus(SeminarAttendanceStatusType statusType) {
    return SeminarAttendanceStatus.builder().type(statusType).build();
  }

  @Nested
  @DisplayName("세미나 참석 테스트")
  class SeminarAttendanceTest {

    @Test
    @DisplayName("DB에 저장된 세미나 참석 정보를 확인한다.")
    void should_check_when_SavedSeminarAttendance() {
      em.clear();
      LocalDateTime now = LocalDateTime.now();
      seminarAttendance = seminarAttendanceRepository.findById(seminarAttendanceId).orElseThrow();

      assertThat(seminarAttendance.getMember().getId()).isEqualTo(member.getId());
      assertThat(seminarAttendance.getMember().getProfile().getStudentId()).isEqualTo(member.getProfile().getStudentId());
      assertThat(seminarAttendance.getSeminar().getId()).isEqualTo(seminar.getId());
      assertThat(seminarAttendance.getSeminar().getName()).isEqualTo(seminar.getName());
      assertThat(seminarAttendance.getSeminar().getAttendanceCode()).isEqualTo(seminar.getAttendanceCode());
      assertThat(seminarAttendance.getSeminarAttendanceStatus().getType()).isEqualTo(statusType);
      assertThat(seminarAttendance.getAttendTime()).isAfter(now.minusDays(2));
    }
  }

  @Nested
  @DisplayName("세미나 지각 사유 및 상태 테스트")
  class SeminarAttendanceExcuseAndStatusTest {

    @Test
    @DisplayName("DB에 세미나 지각 사유를 저장해야 한다.")
    void should_success_when_saveAttendExcuseSeminar() {
      String excuse = "늦게 일어나서";
      seminarAttendance.changeLatenessStatus(excuse);
      em.flush();
      em.clear();

      seminarAttendance = seminarAttendanceRepository.findById(seminarAttendanceId).orElseThrow();
      Long statusId = seminarAttendance.getSeminarAttendanceStatus().getId();
      SeminarAttendanceStatus attendanceStatus = seminarAttendanceStatusRepository.findById(statusId).orElseThrow();
      SeminarAttendanceExcuse attendanceExcuse = seminarAttendanceExcuseRepository.findById(seminarAttendanceId)
          .orElseThrow();

      assertThat(seminarAttendance.getSeminarAttendanceStatus().getType()).isEqualTo(LATENESS);
      assertThat(attendanceStatus.getType()).isEqualTo(LATENESS);
      assertThat(seminarAttendance.getExcuse().get()).isEqualTo(excuse);
      assertThat(attendanceExcuse.getAbsenceExcuse()).isEqualTo(excuse);
    }

    @Test
    @DisplayName("DB에 세미나 지각 사유를 수정해야 한다.")
    void should_success_when_modifyAttendExcuseSeminar() {
      String excuse = "늦게 일어났습니다!";
      seminarAttendance.changeLatenessStatus("늦게 일어나서");
      em.flush();
      em.clear();

      seminarAttendance = seminarAttendanceRepository.findById(seminarAttendanceId).orElseThrow();
      seminarAttendance.changeLatenessStatus(excuse);
      SeminarAttendanceExcuse attendanceExcuse = seminarAttendanceExcuseRepository.findById(seminarAttendanceId)
          .orElseThrow();

      assertThat(attendanceExcuse.getAbsenceExcuse()).isEqualTo(excuse);
      assertThat(seminarAttendance.getExcuse().get()).isEqualTo(excuse);
    }
  }

  @Nested
  @DisplayName("세미나 참석 삭제 테스트")
  class SeminarAttendanceDeleteTest {
    @Test
    @DisplayName("DB에 저장된 세미나 참석 정보를 삭제했을 때 지각 사유도 삭제되어야 한다.")
    void should_deleteSeminarAttendance_when_deleteSeminarAttendanceStatus() {
      seminarAttendance.changeLatenessStatus("늦게 일어나서");
      em.flush();
      em.clear();

      int beforeAttendanceExcuseLength = seminarAttendanceExcuseRepository.findAll().size();
      int beforeAttendanceLength = seminarAttendanceRepository.findAll().size();
      seminarAttendance = seminarAttendanceRepository.findById(seminarAttendanceId).orElseThrow();
      seminarAttendanceRepository.delete(seminarAttendance);

      int afterAttendanceExcuseLength = seminarAttendanceExcuseRepository.findAll().size();
      int afterAttendanceLength = seminarAttendanceRepository.findAll().size();

      assertThat(afterAttendanceExcuseLength).isEqualTo(beforeAttendanceExcuseLength - 1);
      assertThat(afterAttendanceLength).isEqualTo(beforeAttendanceLength - 1);
    }
  }
}
