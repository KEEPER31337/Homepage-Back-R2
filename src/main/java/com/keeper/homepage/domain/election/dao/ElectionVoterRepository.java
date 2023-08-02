package com.keeper.homepage.domain.election.dao;

import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.domain.election.entity.ElectionVoter;
import com.keeper.homepage.domain.election.entity.ElectionVoterPK;
import com.keeper.homepage.domain.member.entity.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionVoterRepository extends JpaRepository<ElectionVoter, ElectionVoterPK> {

  List<ElectionVoter> findByMemberAndElectionAndIsVoted(Member member, Election election,
      boolean isVoted);

}
