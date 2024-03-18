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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

class PointLogServiceTest extends IntegrationTest {

  private Member giver;
  private long pointLogId, otherPointLogId;


  @Nested
  @DisplayName("포인트 내역 조회 테스트")
  class findPointLogTest {

    @BeforeEach
    void setUp() {
      giver = memberTestHelper.generate();
      pointLogId = pointLogTestHelper.builder().member(giver).build().getId();
      otherPointLogId = pointLogTestHelper.builder().member(giver).build().getId();
    }

    @Test
    @DisplayName("포인트 내역 조회를 성공해야 한다.")
    void 포인트_내역_조회를_성공해야_한다() {
      em.flush();
      em.clear();

      Page<PointLog> findPointLogPages = pointLogService.findAllPointLogs(PageRequest.of(0, 10),
          giver.getId());

      assertThat(findPointLogPages
          .map(PointLog::getId)
          .toList())
          .contains(pointLogId, otherPointLogId);
    }
  }
}
