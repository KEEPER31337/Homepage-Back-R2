package com.keeper.homepage.domain.point.application;

import static org.assertj.core.api.Assertions.*;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.point.entity.PointLog;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


class GivePointServiceTest extends IntegrationTest {

  private Member giver, receiver;
  private long giverId, receiverId;

  private static final int GIVEPOINT = 1000;
  private static final String GIVEMESSAGE = "TEST MESSAGE";

  @Nested
  @DisplayName("포인트 선물 기능 테스트")
  class GivePointTest {

    @BeforeEach
    void setUp() {
      giver = memberTestHelper.builder().point(10000).build();
      receiver = memberTestHelper.builder().point(10000).build();
      giverId = giver.getId();
      receiverId = receiver.getId();
    }

    @Test
    @DisplayName("포인트 기부자와 수취인은 점수가 변경되야 한다.")
    void 포인트_기부자와_수취인은_점수가_변경되야_한다() {
      int expectedGiverPoint = giver.getPoint() - GIVEPOINT;
      int expectedReceiverPoint = receiver.getPoint() + GIVEPOINT;
      givePointService.presentPoint(giverId, receiverId, GIVEPOINT, "테스트");

      em.flush();
      em.clear();

      Member findGiver = memberRepository.findById(giverId).orElseThrow();
      Member findReceiver = memberRepository.findById(receiverId).orElseThrow();

      assertThat(findGiver.getPoint()).isEqualTo(expectedGiverPoint);
      assertThat(findReceiver.getPoint()).isEqualTo(expectedReceiverPoint);
    }
  }

  @Nested
  @DisplayName("선물 포인트 로그 기능 테스트")
  class PresentPointLogTest {

    @BeforeEach
    void setUp() {
      giver = memberTestHelper.builder().point(10000).build();
      receiver = memberTestHelper.builder().point(10000).build();
      giverId = giver.getId();
      receiverId = receiver.getId();
    }

    @Test
    @DisplayName("선물 포인트 로그가 만들어져야 한다.")
    void 선물_포인트_로그가_만들어져야_한다() {
      givePointService.presentPoint(giverId, receiverId, GIVEPOINT, GIVEMESSAGE);

      em.flush();
      em.clear();

      List<PointLog> giverPointLogs = giver.getPointLogs();
      List<PointLog> receiverPointLogs = receiver.getPointLogs();

      assertThat(giverPointLogs.stream()
          .map(PointLog::getPoint)
          .collect(Collectors.toList())).contains(-GIVEPOINT);

      assertThat(receiverPointLogs.stream()
          .map(PointLog::getPoint)
          .collect(Collectors.toList())).contains(GIVEPOINT);

      assertThat(giverPointLogs.stream()
          .map(PointLog::getDetail)
          .collect(Collectors.toList())).contains(GIVEMESSAGE);

      assertThat(receiverPointLogs.stream()
          .map(PointLog::getDetail)
          .collect(Collectors.toList())).contains(GIVEMESSAGE);
    }
  }

}
