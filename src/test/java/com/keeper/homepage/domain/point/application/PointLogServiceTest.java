package com.keeper.homepage.domain.point.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.point.entity.PointLog;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PointLogServiceTest extends IntegrationTest {

  private Member giver, receiver;
  private long giverId, receiverId;

  private static final int GIVEPOINT = 1000;
  private static final String GIVEMESSAGE = "TEST MESSAGE";

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
      Long giverPointLogId = pointLogService.create(giver, -GIVEPOINT, GIVEMESSAGE);
      Long receiverPointLogId = pointLogService.create(receiver, GIVEPOINT, GIVEMESSAGE);

      em.flush();
      em.clear();

      PointLog findGiverPointLog = pointLogRepository.findById(giverPointLogId).orElseThrow();
      PointLog findReceiverPointLog = pointLogRepository.findById(receiverPointLogId).orElseThrow();

      assertThat(findGiverPointLog.getPoint()).isEqualTo(-GIVEPOINT);
      assertThat(findReceiverPointLog.getPoint()).isEqualTo(GIVEPOINT);

      assertThat(findGiverPointLog.getMember().getId()).isEqualTo(giverId);
      assertThat(findReceiverPointLog.getMember().getId()).isEqualTo(receiverId);

      assertThat(findGiverPointLog.getDetail()).isEqualTo(GIVEMESSAGE);
      assertThat(findReceiverPointLog.getDetail()).isEqualTo(GIVEMESSAGE);

      assertThat(findGiverPointLog.getIsSpent()).isEqualTo(true);
      assertThat(findReceiverPointLog.getIsSpent()).isEqualTo(false);


    }
  }

  @Nested
  @DisplayName("포인트 내역 조회 테스트")
  class findPointLogTest {

    @Test
    @DisplayName("포인트 내역 조회를 성공해야 한다.")
    void 포인트_내역_조회를_성공해야_한다() {

    }
  }



}
