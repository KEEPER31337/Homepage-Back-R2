package com.keeper.homepage.domain.merit.dao;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.merit.entity.MeritType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.*;

public class MeritTypeRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("상벌점 타입 생성 테스트")
  @Component
  class MeritTypeTest {

    @Test
    @DisplayName("DB에 상벌점 타입 등록을 성공해야 한다.")
    void should_success_when_registerMeritType() {
      MeritType meritType = meritTypeHelper.generate();
      String meritTypeDetail = meritType.getDetail();

      em.flush();
      em.clear();

      MeritType findMeritType = meritTypeRepository.findByDetail(meritTypeDetail).orElseThrow();

      assertThat(meritType.getMerit()).isEqualTo(findMeritType.getMerit());
      assertThat(meritType.getIsMerit()).isEqualTo(findMeritType.getIsMerit());
      assertThat(meritType.getDetail()).isEqualTo(findMeritType.getDetail());
    }
  }
}
