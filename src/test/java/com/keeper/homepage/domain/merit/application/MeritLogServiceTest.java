package com.keeper.homepage.domain.merit.application;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.merit.entity.MeritLog;
import com.keeper.homepage.domain.merit.entity.MeritType;
import java.util.Optional;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class MeritLogServiceTest extends IntegrationTest {

  private Member giver, awarder;
  private MeritType meritType;
  private long giverId, awarderId;
  private long meritLogId, otherMeritLogId, meritTypeId;


  @BeforeEach
  void setUp() {
    giver = memberTestHelper.generate();
    awarder = memberTestHelper.generate();
    meritType = meritTypeHelper.generate();
  }

  @Nested
  @DisplayName("상벌점 부여 테스트")
  class MeritLogTest {

    @Test
    @DisplayName("상벌점 부여를 성공해야 한다.")
    void 상벌점_부여를_성공해야_한다() {
      awarderId = awarder.getId();
      giverId = giver.getId();
      meritTypeId = meritType.getId();

      Long meritLogId = meritLogService.recordMerit(awarderId, giverId, meritTypeId);

      em.flush();
      em.clear();

      MeritLog findMeritLog = meritLogRepository.findById(meritLogId).orElseThrow();

      assertThat(findMeritLog.getId()).isEqualTo(meritLogId);
      assertThat(findMeritLog.getAwarder().getId()).isEqualTo(awarderId);
      assertThat(findMeritLog.getGiver().getId()).isEqualTo(giverId);
      assertThat(findMeritLog.getMeritType().getId()).isEqualTo(meritType.getId());
    }

    @Test
    @DisplayName("상벌점 페이지를 조회할 수 있어야 한다.")
    void 상벌점_페이지를_조회할_수_있어야_한다() {
      meritLogId = meritLogTestHelper.generate().getId();
      otherMeritLogId = meritLogTestHelper.generate().getId();

      em.flush();
      em.clear();

      Page<MeritLog> meritLogs = meritLogService.findAll(PageRequest.of(0, 10));

      assertThat(meritLogs.stream()
          .map(MeritLog::getId)
          .collect(toList())).contains(meritLogId, otherMeritLogId);

    }

    @Test
    @DisplayName("상벌점 페이지를 ID 값으로 조회할 수 있어야 한다.")
    void 상벌점_페이즈를_ID_값으로_조회할_수_있어야_한다() {
      meritLogId = meritLogTestHelper.builder().giver(giver).build().getId();
      otherMeritLogId = meritLogTestHelper.builder().giver(giver).build().getId();

      em.flush();
      em.clear();

      Page<MeritLog> meritLogPage = meritLogService.findByGiver_Id(PageRequest.of(0, 10),
          giver.getId());

      assertThat(meritLogPage.stream()
          .map(MeritLog::getId)
          .collect(toList()))
          .contains(meritLogId, otherMeritLogId);
    }
  }
}
