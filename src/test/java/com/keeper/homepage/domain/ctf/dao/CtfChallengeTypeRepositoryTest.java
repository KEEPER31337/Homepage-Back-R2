package com.keeper.homepage.domain.ctf.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.ctf.entity.challenge.CtfChallengeType;
import com.keeper.homepage.domain.ctf.entity.challenge.CtfChallengeType.ChallengeType;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CtfChallengeTypeRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("문제 종류 테스트")
  class CtfChallengeTypeTest {

    @Test
    @DisplayName("ChallengeType Enum에 DB상의 모든 데이터가 있어야 한다.")
    public void ChallengeType_Enum에_DB상의_모든_데이터가_있어야_한다() throws Exception {
      List<CtfChallengeType> ctfChallengeTypes = ctfChallengeTypeRepository.findAll();
      List<CtfChallengeType> ctfChallengeEnumTypes = Arrays.stream(ChallengeType.values())
          .map(CtfChallengeType::getCtfChallengeType)
          .toList();

      assertThat(getId(ctfChallengeTypes)).containsAll(getId(ctfChallengeEnumTypes));
    }

    private static List<Long> getId(List<CtfChallengeType> ctfChallengeTypes) {
      return ctfChallengeTypes.stream()
          .map(CtfChallengeType::getId)
          .toList();
    }
  }
}
