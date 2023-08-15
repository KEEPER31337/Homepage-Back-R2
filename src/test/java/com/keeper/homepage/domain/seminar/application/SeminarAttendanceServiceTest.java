package com.keeper.homepage.domain.seminar.application;

import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_ATTEMPT_NOT_AVAILABLE;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_ATTENDANCE_CODE_NOT_AVAILABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.merit.entity.MeritLog;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.global.error.BusinessException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
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
      long seminarId = seminarService.save().id();
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
  @DisplayName("세미나 지각 & 결석 벌점 테스트")
  class SeminarDemeritTest {

    private Member member;
    private static final long SEMINAR_ABSENCE_MERIT_TYPE_ID = 2;
    private static final long SEMINAR_DUAL_LATENESS_MERIT_TYPE_ID = 3;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.generate();
    }

    @Test
    @DisplayName("세미나 출석일 경우 벌점이 부여되지 않아야 한다.")
    public void 세미나_출석일_경우_벌점이_부여되지_않아야_한다() throws Exception {
      //given
      LocalDateTime now = LocalDateTime.now();

      long seminarId = seminarService.save().id();
      String attendanceCode = seminarService.start(seminarId, now.plusMinutes(5), now.plusMinutes(10)).attendanceCode();

      //when
      seminarAttendanceService.attendance(seminarId, member, attendanceCode);

      em.flush();
      em.clear();

      //then
      assertThat(meritLogRepository.findByMemberId(member.getId())).isEmpty();
    }

    @Test
    @DisplayName("세미나 결석일 경우 벌점이 부여되어야 한다.")
    public void 세미나_결석일_경우_벌점이_부여되어야_한다() throws Exception {
      //given
      LocalDateTime now = LocalDateTime.now();

      long seminarId = seminarService.save().id();
      String attendanceCode = seminarService.start(seminarId, now.minusMinutes(10), now.minusMinutes(5))
          .attendanceCode();

      //when
      seminarAttendanceService.attendance(seminarId, member, attendanceCode);

      em.flush();
      em.clear();

      //then
      MeritLog meritLog = meritLogRepository.findByMemberId(member.getId()).orElseThrow();
      assertThat(meritLog.getMeritType().getId()).isEqualTo(SEMINAR_ABSENCE_MERIT_TYPE_ID);
    }

    @Test
    @DisplayName("세미나 지각 1회일 경우 벌점이 부여되지 않아야 한다.")
    public void 세미나_지각_1회일_경우_벌점이_부여되지_않아야_한다() throws Exception {
      //given
      LocalDateTime now = LocalDateTime.now();

      long seminarId = seminarService.save().id();
      String attendanceCode = seminarService.start(seminarId, now.minusMinutes(5), now.plusDays(5))
          .attendanceCode();

      //when
      seminarAttendanceService.attendance(seminarId, member, attendanceCode);

      em.flush();
      em.clear();

      //then
      assertThat(meritLogRepository.findByMemberId(member.getId())).isEmpty();
    }

    @Test
    @DisplayName("세미나 지각 2회일 경우 벌점이 부여되어야 한다.")
    public void 세미나_지각_2회일_경우_벌점이_부여되어야_한다() throws Exception {
      //given
      LocalDateTime now = LocalDateTime.now();

      long seminarId = seminarService.save().id();
      String attendanceCode = seminarService.start(seminarId, now.minusMinutes(5), now.plusDays(5))
          .attendanceCode();

      long otherSeminarId = seminarService.save().id();
      String otherAttendanceCode = seminarService.start(otherSeminarId, now.minusMinutes(5), now.plusDays(5))
          .attendanceCode();

      //when
      seminarAttendanceService.attendance(seminarId, member, attendanceCode);
      em.flush();
      em.clear();

      member = memberRepository.findById(member.getId()).orElseThrow();
      seminarAttendanceService.attendance(otherSeminarId, member, otherAttendanceCode);

      em.flush();
      em.clear();

      //then
      MeritLog meritLog = meritLogRepository.findByMemberId(member.getId()).orElseThrow();
      assertThat(meritLog.getMeritType().getId()).isEqualTo(SEMINAR_DUAL_LATENESS_MERIT_TYPE_ID);
    }
  }
}
