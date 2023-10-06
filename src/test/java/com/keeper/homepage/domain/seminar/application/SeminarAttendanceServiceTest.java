package com.keeper.homepage.domain.seminar.application;

import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.ABSENCE;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.BEFORE_ATTENDANCE;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.LATENESS;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.getSeminarAttendanceStatusBy;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_ATTEMPT_NOT_AVAILABLE;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_CODE_NOT_AVAILABLE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import com.keeper.homepage.global.error.BusinessException;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SeminarAttendanceServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("출석코드 입력 제한 테스트")
  class AttemptLimitTest {


    @Test
    @DisplayName("출석코드 5회 초과 입력 시 출석이 불가능해야 한다.")
    public void 출석코드_5회_초과_입력_시_출석이_불가능해야_한다() throws Exception {
      Member member = memberTestHelper.generate();
      long seminarId = seminarService.save(LocalDate.now()).id();
      Seminar seminar = seminarRepository.findById(seminarId).orElseThrow();

      String invalidAttendanceCode = "12345";

      for (int i = 0; i < 5; i++) {
        assertThatThrownBy(() -> seminarAttendanceService.attendance(seminar.getId(), member, invalidAttendanceCode))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(SEMINAR_ATTENDANCE_CODE_NOT_AVAILABLE.getMessage());
      }

      assertThatThrownBy(
          () -> seminarAttendanceService.attendance(seminar.getId(), member, seminar.getAttendanceCode()))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(SEMINAR_ATTENDANCE_ATTEMPT_NOT_AVAILABLE.getMessage());
    }
  }

  @Nested
  @DisplayName("세미나 출석 상태 변경 테스트")
  class ChangeAttendanceStatusTest {

    @Test
    @DisplayName("지각, 개인사정으로 변경할 경우 사유도 변경되어야 한다.")
    public void 지각_개인사정으로_변경할_경우_사유도_변경되어야_한다() throws Exception {
      //given
      SeminarAttendance seminarAttendance = seminarAttendanceTestHelper.builder()
          .seminarAttendanceStatus(getSeminarAttendanceStatusBy(BEFORE_ATTENDANCE))
          .build();
      long seminarAttendanceId = seminarAttendance.getId();
      String excuse = "늦잠";

      //when
      seminarAttendanceService.changeStatus(seminarAttendance.getId(), LATENESS, excuse);
      em.flush();
      em.clear();

      //then
      SeminarAttendance findSeminarAttendance = seminarAttendanceRepository.findById(seminarAttendanceId).orElseThrow();
      assertThat(findSeminarAttendance.getSeminarAttendanceStatus().getType()).isEqualTo(LATENESS);
      assertThat(findSeminarAttendance.getSeminarAttendanceExcuse()).isNotNull();
      assertThat(findSeminarAttendance.getSeminarAttendanceExcuse().getAbsenceExcuse()).isEqualTo(excuse);
    }

    @Test
    @DisplayName("지각, 개인사정 외의 출석 상태 타입으로 변경할 경우 사유는 변경되지 않는다.")
    public void 지각_개인사정_외의_출석_상태_타입으로_변경할_경우_사유는_변경되지_않는다() throws Exception {
      //given
      SeminarAttendance seminarAttendance = seminarAttendanceTestHelper.builder()
          .seminarAttendanceStatus(getSeminarAttendanceStatusBy(BEFORE_ATTENDANCE))
          .build();
      long seminarAttendanceId = seminarAttendance.getId();

      //when
      seminarAttendanceService.changeStatus(seminarAttendance.getId(), ABSENCE, null);
      em.flush();
      em.clear();

      //then
      SeminarAttendance findSeminarAttendance = seminarAttendanceRepository.findById(seminarAttendanceId).orElseThrow();
      assertThat(findSeminarAttendance.getSeminarAttendanceStatus().getType()).isEqualTo(ABSENCE);
      assertThat(findSeminarAttendance.getSeminarAttendanceExcuse()).isNull();
    }
  }
}
