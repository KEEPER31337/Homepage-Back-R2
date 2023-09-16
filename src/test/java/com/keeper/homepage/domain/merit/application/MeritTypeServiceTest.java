package com.keeper.homepage.domain.merit.application;

import static com.keeper.homepage.global.error.ErrorCode.*;
import static com.keeper.homepage.global.error.ErrorCode.MERIT_TYPE_DETAIL_DUPLICATE;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.merit.entity.MeritType;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.error.ErrorCode;
import org.junit.jupiter.api.Assertions;
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

    @Test
    @DisplayName("사유 이름을 변경하지 않으면 수정에 성공해야 한다.")
    public void 사유_이름을_변경하지_않으면_수정에_성공해야_한다() {
      MeritType meritType = meritTypeHelper.builder().detail("변경 전 사유").build();

      em.flush();
      em.clear();

      meritTypeService.updateMeritType(meritType.getId(), 2, "변경 전 사유");
      MeritType findMeritType = meritTypeRepository.findById(meritType.getId())
          .orElseThrow();
      assertThat(findMeritType.getMerit()).isEqualTo(2);
      assertThat(findMeritType.getDetail()).isEqualTo("변경 전 사유");
    }

    @Test
    @DisplayName("사유 이름이 중복되면 예외를 던진다.")
    public void 사유_이름이_중복되면_예외를_던진다() {
      meritTypeHelper.builder().detail("변경 전 사유").build();
      MeritType otherMeritType = meritTypeHelper.builder().detail("변경 후 사유").build();

      em.flush();
      em.clear();

      assertThrows(BusinessException.class,
          () -> meritTypeService.updateMeritType(otherMeritType.getId(), 3, "변경 전 사유"),
          MERIT_TYPE_DETAIL_DUPLICATE.getMessage());
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
