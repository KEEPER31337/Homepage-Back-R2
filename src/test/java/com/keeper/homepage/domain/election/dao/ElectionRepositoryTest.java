package com.keeper.homepage.domain.election.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.domain.election.entity.ElectionVoter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Disabled
public class ElectionRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("선거 관리 테스트")
  class ElectionTest {

    @Test
    @DisplayName("유권자 목록 수 확인")
    public void getVotersInElection() throws Exception {
      ElectionVoter electionVoter = electionVoterTestHelper.builder()
          .isVoted(false)
          .build();

      em.flush();
      em.clear();

      Election getVoters = electionRepository.getReferenceById(electionVoter.getElection().getId());

      assertThat(getVoters.getIsAvailable()).isEqualTo(true);
      assertThat(getVoters.getElectionVoters().size()).isEqualTo(1);
    }
  }
}
