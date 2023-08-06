package com.keeper.homepage.domain.election.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.domain.election.entity.ElectionVoter;
import com.keeper.homepage.domain.member.entity.Member;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ElectionVoterRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("선거 투표자 관리 테스트")
  class ElectionVoterTest {

    @Test
    @DisplayName("투표 완료 인원 목록 수 확인")
    public void getVoterIsVoted() throws Exception {
      Member member = memberTestHelper.generate();
      Election election = electionTestHelper.generate();
      ElectionVoter electionVoter = electionVoterTestHelper.builder()
          .member(member)
          .election(election)
          .isVoted(true)
          .build();

      em.flush();
      em.clear();

      ElectionVoter votedVoters = electionVoterRepository.findByMemberAndElectionAndIsVoted(
          member, election, true);

      assertThat(votedVoters.getElection().getElectionVoters().size()).isEqualTo(1);
    }
  }
}
