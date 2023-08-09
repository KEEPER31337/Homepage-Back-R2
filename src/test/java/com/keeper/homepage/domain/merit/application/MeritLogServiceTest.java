package com.keeper.homepage.domain.merit.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.merit.entity.MeritLog;
import com.keeper.homepage.domain.merit.entity.MeritType;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MeritLogServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("상벌점 부여 테스트")
  class MeritLogTest {

    @Test
    @DisplayName("상벌점 부여를 성공해야 한다.")
    void 상벌점_부여를_성공해야_한다() {
      Long awarderId = memberTestHelper.generate().getId();
      Long giverId = memberTestHelper.generate().getId();
      MeritType meritType = meritTypeHelper.generate();

      Long meritLogId = meritLogService.recordMerit(awarderId, giverId, "무단 결석");

      em.flush();
      em.clear();

      MeritLog findMeritLog = meritLogRepository.findById(meritLogId).orElseThrow();

      assertThat(findMeritLog.getId()).isEqualTo(meritLogId);
      assertThat(findMeritLog.getAwarder().getId()).isEqualTo(awarderId);
      assertThat(findMeritLog.getGiver().getId()).isEqualTo(giverId);
      assertThat(findMeritLog.getMeritType().getId()).isEqualTo(meritType.getId());
    }
  }
}