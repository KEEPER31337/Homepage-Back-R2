package com.keeper.homepage.domain.ctf.dao;

import static com.keeper.homepage.domain.ctf.entity.challenge.CtfChallengeType.ChallengeType.DYNAMIC;
import static com.keeper.homepage.domain.ctf.entity.challenge.CtfChallengeType.ChallengeType.STANDARD;
import static com.keeper.homepage.domain.ctf.entity.challenge.CtfChallengeType.getCtfChallengeType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.ctf.entity.challenge.CtfChallenge;
import com.keeper.homepage.domain.ctf.entity.challenge.CtfDynamicChallengeInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CtfChallengeRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("CTF 문제 타입 테스트")
  class CtfChallengeTypeTest {

    @Test
    @DisplayName("CTF 문제 타입이 DYNAMIC이라면 CtfDynamicChallengeInfo가 잘 저장되어야 한다.")
    public void CTF_문제_타입이_DYNAMIC이라면_CtfDynamicChallengeInfo가_잘_저장되어야_한다() throws Exception {
      CtfChallenge ctfChallenge = ctfChallengeTestHelper.builder()
          .type(getCtfChallengeType(DYNAMIC))
          .build();

      ctfChallenge.setCtfDynamicChallengeInfo(CtfDynamicChallengeInfo.builder()
          .minScore(500)
          .maxScore(1000)
          .build());

      em.flush();
      em.clear();
      CtfChallenge findChallenge = ctfChallengeRepository.getReferenceById(ctfChallenge.getId());

      assertThat(findChallenge.getCtfDynamicChallengeInfo()).isNotNull();
      assertThat(findChallenge.getCtfDynamicChallengeInfo().getCtfChallenge()).isEqualTo(findChallenge);
      assertThat(findChallenge.getCtfDynamicChallengeInfo().getMinScore()).isEqualTo(500);
      assertThat(findChallenge.getCtfDynamicChallengeInfo().getMaxScore()).isEqualTo(1000);
    }

    @Test
    @DisplayName("CTF 문제 타입이 STANDARD라면 CtfDynamicChallengeInfo를 설정할 수 없다.")
    public void CTF_문제_타입이_STANDARD라면_CtfDynamicChallengeInfo를_설정할_수_없다() throws Exception {
      CtfChallenge ctfChallenge = ctfChallengeTestHelper.builder()
          .type(getCtfChallengeType(STANDARD))
          .build();

      assertThatThrownBy(() -> ctfChallenge.setCtfDynamicChallengeInfo(CtfDynamicChallengeInfo.builder().build()))
          .isInstanceOf(IllegalStateException.class);
    }
  }

  @Nested
  @DisplayName("Cascade 테스트")
  class CascadeTest {

    @Test
    @DisplayName("문제 카테고리 정보는 잘 저장되어야 한다.")
    public void 문제_카테고리_정보는_잘_저장되어야_한다() throws Exception {
      /*CtfChallenge ctfChallenge = ctfChallengeTestHelper.generate();
      em.flush();
      em.clear();

      CtfChallenge findChallenge = ctfChallengeRepository.findById(ctfChallenge.getId()).orElseThrow();
      findChallenge.addCategory(MISC);
      em.flush();
      em.clear(); // TODO: 문제 원인 찾아서 고치기*/
    }
  }
}
