package com.keeper.homepage.domain.merit.application;

import static org.assertj.core.api.Assertions.*;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.merit.entity.MeritType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


class MeritTypeServiceTest extends IntegrationTest {

  private MeritType meritType, otherMeritType;
  private long meritTypeId, otherMeritTypeId;

  @BeforeEach
  void setUp() {
    meritType = meritTypeHelper.generate();
    otherMeritType = meritTypeHelper.generate();
    meritTypeId = meritType.getId();
    otherMeritTypeId = otherMeritType.getId();
  }

  @Nested
  @DisplayName("상벌점 타입 추가")
  class AddMeritType {

    @Test
    @DisplayName("상벌점 타입 추가가 성공해야 한다.")
    void 상벌점_타입_추가가_성공해야_한다() {
      meritTypeId = meritTypeService.addMeritType(3, "우수기술문서 작성");
      otherMeritTypeId = meritTypeService.addMeritType(-3, "무단 결석");

      em.flush();
      em.clear();

      MeritType findMeritType = meritTypeRepository.findById(meritTypeId).orElseThrow();
      MeritType findOtherMeritType = meritTypeRepository.findById(otherMeritTypeId).orElseThrow();

      assertThat(findMeritType.getMerit()).isEqualTo(3);
      assertThat(findOtherMeritType.getMerit()).isEqualTo(-3);

      assertThat(findMeritType.getDetail()).isEqualTo("우수기술문서 작성");
      assertThat(findOtherMeritType.getDetail()).isEqualTo("무단 결석");
    }
  }

  @Nested
  @DisplayName("상벌점 수정")
  class UpdateMeritType {

    @Test
    @DisplayName("상벌점 수정이 성공해야 한다.")
    void 상벌점_수정이_성공해야_한다() {
      em.flush();
      em.clear();

      meritTypeService.updateMeritType(meritTypeId, -1, "수정된 사유");
      MeritType updatedMeritType = meritTypeRepository.findById(meritTypeId).orElseThrow();
      assertThat(updatedMeritType.getMerit()).isEqualTo(-1);
      assertThat(updatedMeritType.getDetail()).isEqualTo("수정된 사유");
    }
  }

  @Nested
  @DisplayName("상벌점 타입 조회")
  class SearchMeritType {

    @Test
    @DisplayName("모든 상벌점 타입을 조회할 수 있어야 한다.")
    void 모든_상벌점_타입을_조회할_수_있어야_한다() {
      em.flush();
      em.clear();

      Page<MeritType> findPages = meritTypeService.findAll(PageRequest.of(0, 10));

      assertThat(findPages.stream()
          .map(MeritType::getId)
          .toList())
          .contains(meritTypeId, otherMeritTypeId);
    }
  }
}
