package com.keeper.homepage.domain.election.dao;

import com.keeper.homepage.domain.election.entity.ElectionVoter;
import com.keeper.homepage.domain.election.entity.ElectionVoterPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionVoterRepository extends JpaRepository<ElectionVoter, ElectionVoterPK> {

}
