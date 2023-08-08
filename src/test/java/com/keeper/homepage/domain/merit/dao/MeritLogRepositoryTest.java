package com.keeper.homepage.domain.merit.dao;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.merit.entity.MeritLog;
import com.keeper.homepage.domain.merit.entity.MeritType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class MeritLogRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("상벌점 기능 테스트")
  @Component
  class MeritTest {

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


}
