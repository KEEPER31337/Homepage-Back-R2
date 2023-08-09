package com.keeper.homepage.domain.merit.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.merit.entity.MeritType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


class MeritTypeServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("상벌점 수정")
  class UpdateMeritType {

    @Test
    @DisplayName("상벌점 수정이 성공해야 한다.")
    void 상벌점_수정이_성공해야_한다() {
      Long meritTypeId = meritTypeHelper.generate().getId();
      MeritType findMeritType = meritTypeRepository.findById(meritTypeId).orElseThrow();
      findMeritType.update(1, "수정된 사유");

      em.flush();
      em.clear();

      MeritType updatedMeritType = meritTypeRepository.findById(meritTypeId).orElseThrow();
      assertThat(updatedMeritType.getMerit()).isEqualTo(1);
      assertThat(updatedMeritType.getDetail()).isEqualTo("수정된 사유");
    }
  }

}
