package com.keeper.homepage.domain.vote.dao;

import com.keeper.homepage.domain.vote.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {

}
