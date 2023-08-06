package com.keeper.homepage.domain.election.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.domain.election.entity.ElectionVoter;
import com.keeper.homepage.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


public class ElectionRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("선거 관리 테스트")
  class ElectionTest {

    @Test
    @DisplayName("유권자 목록 수 확인")
    public void getVotersInElection() throws Exception {
      Member member = memberTestHelper.generate();
      Election election = electionTestHelper.generate();
      ElectionVoter electionVoter = electionVoterTestHelper.builder()
          .member(member)
          .election(election)
          .isVoted(false)
          .build();

      em.flush();
      em.clear();

      Election getVoters = electionRepository.getReferenceById(election.getId());

      assertThat(getVoters.getElectionVoters().size()).isEqualTo(1);
    }
  }
}
