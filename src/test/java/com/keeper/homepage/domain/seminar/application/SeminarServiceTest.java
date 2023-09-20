package com.keeper.homepage.domain.seminar.application;

import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_IS_DUPLICATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarIdResponse;
import com.keeper.homepage.global.error.BusinessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
      Long seminarId = seminarService.getRecentlyDoneSeminar().id();

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
      Long todaySeminarId = seminarTestHelper.builder()
          .openTime(LocalDateTime.now())
          .latenessCloseTime(LocalDateTime.now())
          .build()
          .getId();

      //when
      Long seminarId = seminarService.getRecentlyDoneSeminar().id();

      //then
      assertThat(seminarId).isEqualTo(yesterdaySeminarId);
    }

    @Test
    @DisplayName("마감된 세미나 중 시작시간 기준으로 현재와 가장 가까운 세미나를 불러와야 한다.")
    public void 마감된_세미나_중_시작시간_기준으로_현재와_가장_가까운_세미나를_불러와야_한다() throws Exception {
      seminarTestHelper.builder()
          .openTime(LocalDateTime.now().minusDays(2))
          .latenessCloseTime(LocalDateTime.now().minusDays(2))
          .build();
      Long recentSeminarId = seminarTestHelper.builder()
          .openTime(LocalDateTime.now().minusDays(1))
          .latenessCloseTime(LocalDateTime.now().minusDays(1))
          .build()
          .getId();
      seminarTestHelper.builder()
          .openTime(LocalDateTime.now().minusDays(3))
          .latenessCloseTime(LocalDateTime.now().minusDays(3))
          .build();

      Long seminarId = seminarService.getRecentlyDoneSeminar().id();

      assertThat(seminarId).isEqualTo(recentSeminarId);
    }
  }

  @Nested
  @DisplayName("가장 최근의 예정된 세미나 조회 테스트")
  class GetRecentlyUpcomingSeminarTest {

    @Test
    @DisplayName("오늘을 포함하여 예정된 최신 세미나 2개를 조회해야 한다.")
    public void 오늘을_포함하여_예정된_최신_세미나_2개를_조회해야_한다() throws Exception {
      Long yesterdaySeminarId = seminarTestHelper.builder()
          .openTime(LocalDateTime.now().minusDays(1))
          .build()
          .getId();
      Long seminarId1 = seminarTestHelper.builder()
          .openTime(LocalDateTime.now())
          .build()
          .getId();
      Long seminarId2 = seminarTestHelper.builder()
          .openTime(LocalDateTime.now().plusDays(1))
          .build()
          .getId();

      List<SeminarIdResponse> recentlyUpcomingSeminars = seminarService.getRecentlyUpcomingSeminars();

      assertThat(recentlyUpcomingSeminars).hasSize(2);
      assertThat(recentlyUpcomingSeminars.get(0).id()).isEqualTo(seminarId1);
      assertThat(recentlyUpcomingSeminars.get(1).id()).isEqualTo(seminarId2);
    }
  }

  @Nested
  @DisplayName("세미나 생성 테스트")
  class CreateSeminarTest {

    @Test
    @DisplayName("세미나는 하루에 하나만 생성되어야 한다.")
    public void 세미나는_하루에_하나만_생성되어야_한다() throws Exception {
      //given
      seminarTestHelper.builder().openTime(LocalDateTime.now()).build();
      em.flush();
      em.clear();

      //then
      assertThrows(BusinessException.class, () -> {
        seminarService.save(LocalDate.now());
      }, SEMINAR_IS_DUPLICATED.getMessage());
    }
  }
}
