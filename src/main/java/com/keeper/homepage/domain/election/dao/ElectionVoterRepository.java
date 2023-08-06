package com.keeper.homepage.domain.election.dao;

import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.domain.election.entity.ElectionVoter;
import com.keeper.homepage.domain.election.entity.ElectionVoterPK;
import com.keeper.homepage.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionVoterRepository extends JpaRepository<ElectionVoter, ElectionVoterPK> {

  Optional <ElectionVoter> findByMemberAndElectionAndIsVoted(Member member, Election election, Boolean isVoted);

}
