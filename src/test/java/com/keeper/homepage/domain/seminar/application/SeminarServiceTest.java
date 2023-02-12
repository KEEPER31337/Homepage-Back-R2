package com.keeper.homepage.domain.seminar.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SeminarServiceTest extends IntegrationTest {

  @Test
  @DisplayName("출석 코드의 길이가 4인지 확인한다.")
  public void 출석_코드의_길이가_4인지_확인한다() throws Exception {
    String attendanceCode = seminarService.randomAttendanceCode();
    assertThat(attendanceCode.length()).isEqualTo(4);
  }
}
