package com.keeper.homepage.domain.ctf.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.ctf.entity.challenge.CtfChallengeCategory;
import com.keeper.homepage.domain.ctf.entity.challenge.CtfChallengeCategory.CtfChallengeCategoryType;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CtfChallengeCategoryRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("문제 카테고리 종류 테스트")
  class CtfChallengeCategoryTest {

    @Test
    @DisplayName("CtfChallengeCategoryType Enum에 DB의 모든 데이터가 있어야 한다.")
    public void CtfChallengeCategoryType_Enum에_DB의_모든_데이터가_있어야_한다() throws Exception {
      List<CtfChallengeCategory> ctfChallengeCategories = ctfChallengeCategoryRepository.findAll();
      List<CtfChallengeCategory> ctfChallengeCategoryTypes = Arrays.stream(
              CtfChallengeCategoryType.values())
          .map(CtfChallengeCategory::getCtfChallengeCategoryBy)
          .toList();

      assertThat(getId(ctfChallengeCategories)).containsAll(getId(ctfChallengeCategoryTypes));
      assertThat(getType(ctfChallengeCategories)).containsAll(getType(ctfChallengeCategoryTypes));
    }

    private static List<Long> getId(List<CtfChallengeCategory> ctfChallengeCategories) {
      return ctfChallengeCategories.stream()
          .map(CtfChallengeCategory::getId)
          .toList();
    }

    private static List<String> getType(List<CtfChallengeCategory> ctfChallengeCategoryTypes) {
      return ctfChallengeCategoryTypes.stream()
          .map(CtfChallengeCategory::getType)
          .map(CtfChallengeCategoryType::getType)
          .toList();
    }
  }
}
