package com.keeper.homepage.domain.seminar.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.seminar.dto.request.SeminarAttendanceCodeRequest;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.global.error.BusinessException;
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
      Seminar seminar = seminarTestHelper.generate();
      Member member = memberTestHelper.generate();

      String invalidAttendanceCode = "12345";

      for (int i = 0; i < 5; i++) {
        assertThatThrownBy(() -> seminarAttendanceService.save(seminar.getId(), member,
            new SeminarAttendanceCodeRequest(invalidAttendanceCode)))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("올바르지 않은 출석 코드입니다.");
      }

      assertThatThrownBy(() -> seminarAttendanceService.save(seminar.getId(), member,
          new SeminarAttendanceCodeRequest(seminar.getAttendanceCode())))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining("출석코드 입력 가능 횟수를 초과하여 출석이 불가능합니다.");
    }
  }
}
