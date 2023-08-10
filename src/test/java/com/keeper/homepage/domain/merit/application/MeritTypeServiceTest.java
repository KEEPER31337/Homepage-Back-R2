package com.keeper.homepage.domain.merit.application;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.merit.entity.MeritType;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


class MeritTypeServiceTest extends IntegrationTest {

  MeritType meritType, otherMeritType;

  @Nested
  @DisplayName("상벌점 타입 추가")
  class AddMeritType {

    @Test
    @DisplayName("상벌점 타입 추가가 성공해야 한다.")
    void 상벌점_타입_추가가_성공해야_한다() {
      Long meritTypeId1 = meritTypeService.addMeritType(3, "우수기술문서 작성");
      Long meritTypeId2 = meritTypeService.addMeritType(-3, "무단 결석");

      em.flush();
      em.clear();

      MeritType findMeritType1 = meritTypeRepository.findById(meritTypeId1).orElseThrow();
      MeritType findMeritType2 = meritTypeRepository.findById(meritTypeId2).orElseThrow();

      assertThat(findMeritType1.getMerit()).isEqualTo(3);
      assertThat(findMeritType2.getMerit()).isEqualTo(-3);

      assertThat(findMeritType1.getDetail()).isEqualTo("우수기술문서 작성");
      assertThat(findMeritType2.getDetail()).isEqualTo("무단 결석");

      assertThat(findMeritType1.getIsMerit()).isEqualTo(true);
      assertThat(findMeritType2.getIsMerit()).isEqualTo(false);
    }
  }

  @Nested
  @DisplayName("상벌점 수정")
  class UpdateMeritType {

    @Test
    @DisplayName("상벌점 수정이 성공해야 한다.")
    void 상벌점_수정이_성공해야_한다() {
      Long meritTypeId = meritTypeHelper.generate().getId();

      em.flush();
      em.clear();

      meritTypeService.updateMeritType(meritTypeId, -1, "수정된 사유");
      MeritType updatedMeritType = meritTypeRepository.findById(meritTypeId).orElseThrow();
      assertThat(updatedMeritType.getMerit()).isEqualTo(-1);
      assertThat(updatedMeritType.getIsMerit()).isEqualTo(false);
      assertThat(updatedMeritType.getDetail()).isEqualTo("수정된 사유");
    }
  }

  @Nested
  @DisplayName("상벌점 타입 조회")
  class SearchMeritType {

    @Test
    @DisplayName("모든 상벌점 타입을 조회할 수 있어야 한다.")
    void 모든_상벌점_타입을_조회할_수_있어야_한다() {
      meritType = meritTypeHelper.generate();
      otherMeritType = meritTypeHelper.generate();

      em.flush();
      em.clear();

      Page<MeritType> findPages = meritTypeService.findAll(PageRequest.of(0, 10));

      assertThat(findPages.stream()
          .map(MeritType::getId)
          .collect(toList()))
          .contains(meritType.getId(), otherMeritType.getId());
    }
  }
}
