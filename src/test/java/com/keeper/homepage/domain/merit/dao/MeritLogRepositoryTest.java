package com.keeper.homepage.domain.merit.dao;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.merit.entity.MeritLog;
import com.keeper.homepage.domain.merit.entity.MeritType;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class MeritLogRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("상벌점 기능 테스트")
  class MeritLogSaveTest {

    @Test
    @DisplayName("DB에 상벌점 로그 등록을 성공해야 한다.")
    void should_success_when_registerMeritType() {
      MeritLog meritLog = meritLogTestHelper.generate();

      em.flush();
      em.clear();

      MeritLog findMeritLog = meritLogRepository.findById(meritLog.getId()).orElseThrow();

      assertThat(meritLog.getId()).isEqualTo(findMeritLog.getId());
      assertThat(meritLog.getGiver()).isEqualTo(findMeritLog.getGiver());
      assertThat(meritLog.getAwarder()).isEqualTo(findMeritLog.getAwarder());
      assertThat(meritLog.getTime()).isBefore(findMeritLog.getTime());
      assertThat(meritLog.getMeritType().getId()).isEqualTo(findMeritLog.getMeritType().getId());

    }
  }

  @Nested
  @DisplayName("상벌점 로그 조회 테스트")
  class MeritLogSearchTest {

    @Test
    @DisplayName("상벌점 로그를 수여자 ID로 조회할 수 있어야 한다.")
    void 상벌점_로그를_수여자_ID로_조회할_수_있어야_한다() {
      Member giver = memberTestHelper.generate();
      Long meritLogIdByGiver = meritLogTestHelper.builder().giver(giver).build().getId();
      meritLogTestHelper.generate();
      meritLogTestHelper.generate();

      em.flush();
      em.clear();

      Page<MeritLog> pages = meritLogRepository.findByGiver_Id(PageRequest.of(0, 10),
          giver.getId());

      assertThat(pages.stream().map(MeritLog::getId).collect(toList()))
          .contains(meritLogIdByGiver);

    }
  }


}
