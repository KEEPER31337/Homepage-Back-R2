package com.keeper.homepage.domain.seminar.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SeminarServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("가장 최근에 마감된 세미나 조회 테스트")
  class GetRecentlyDoneSeminarTest {


    @Test
    @DisplayName("지각 마감시간이 지난 세미나를 불러와야 한다.")
    public void 지각_마감시간이_지난_세미나를_불러와야_한다() throws Exception {
      //given
      Long ongoingSeminarId = seminarTestHelper.builder()
          .openTime(LocalDateTime.now().minusDays(2))
          .latenessCloseTime(LocalDateTime.now().plusDays(1))
          .build()
          .getId();
      Long doneSeminarId = seminarTestHelper.builder()
          .openTime(LocalDateTime.now().minusDays(1))
          .latenessCloseTime(LocalDateTime.now().minusDays(1))
          .build()
          .getId();

      //when
      Long seminarId = seminarService.getRecentlyDoneSeminarId();

      //then
      assertThat(seminarId).isEqualTo(doneSeminarId);
    }

    @Test
    @DisplayName("오늘 날짜의 세미나는 불러오면 안된다.")
    public void 오늘_날짜의_세미나는_불러오면_안된다() throws Exception {
      //given
      Long yesterdaySeminarId = seminarTestHelper.builder()
          .openTime(LocalDateTime.now().minusDays(1))
          .latenessCloseTime(LocalDateTime.now().minusDays(1))
          .build()
          .getId();
      Long todaySeminarId = seminarTestHelper.builder().
          openTime(LocalDateTime.now())
          .latenessCloseTime(LocalDateTime.now())
          .build()
          .getId();

      //when
      Long seminarId = seminarService.getRecentlyDoneSeminarId();

      //then
      assertThat(seminarId).isEqualTo(yesterdaySeminarId);
    }

    @Test
    @DisplayName("마감된 세미나 중 시작시간 기준으로 현재와 가장 가까운 세미나를 불러와야 한다.")
    public void 마감된_세미나_중_시작시간_기준으로_현재와_가장_가까운_세미나를_불러와야_한다() throws Exception {
      seminarTestHelper.builder().
          openTime(LocalDateTime.now().minusDays(2))
          .latenessCloseTime(LocalDateTime.now().minusDays(2))
          .build();

      Long recentSeminarId = seminarTestHelper.builder().
          openTime(LocalDateTime.now().minusDays(1))
          .latenessCloseTime(LocalDateTime.now().minusDays(1))
          .build()
          .getId();

      seminarTestHelper.builder().
          openTime(LocalDateTime.now().minusDays(3))
          .latenessCloseTime(LocalDateTime.now().minusDays(3))
          .build();

      Long seminarId = seminarService.getRecentlyDoneSeminarId();

      assertThat(seminarId).isEqualTo(recentSeminarId);
    }
  }
}
