package com.keeper.homepage.domain.attendance.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.error.BusinessException;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class AttendanceServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("출석 테스트")
  class AttendanceTest {

    @Test
    @DisplayName("출석할 경우 Redis에서 Rank가 계산된다.")
    public void 출석할_경우_Redis에서_Rank가_계산된다() throws Exception {
      Member member1 = memberTestHelper.generate();
      Member member2 = memberTestHelper.generate();
      redisUtil.flushAll();
      attendanceService.create(member1);
      attendanceService.create(member2);

      LocalDate now = LocalDate.now();
      Optional<String> data = redisUtil.getData("attendance:" + now, String.class);

      assertThat(data).isNotEmpty();
      assertThat(data.get()).isEqualTo(String.valueOf(2));
    }

    @Test
    @DisplayName("출석할 경우 포인트 내역도 성공적으로 남아야 한다.")
    public void 출석할_경우_포인트_내역도_성공적으로_남아야_한다() throws Exception {
      Member member = memberTestHelper.generate();
      attendanceService.create(member);

      assertThat(pointLogRepository.findByMember(member)).isNotEmpty();
    }

    @Test
    @DisplayName("오늘 이미 출석한 경우 출석은 실패한다.")
    public void 오늘_이미_출석한_경우_출석은_실패한다() throws Exception {
      Member member = memberTestHelper.generate();
      attendanceService.create(member);

      assertThrows(BusinessException.class, () -> {
        attendanceService.create(member);
      });
    }
  }
}
