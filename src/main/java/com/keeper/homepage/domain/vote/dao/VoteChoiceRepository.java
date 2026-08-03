package com.keeper.homepage.domain.vote.dao;

import com.keeper.homepage.domain.vote.entity.VoteChoice;
import com.keeper.homepage.domain.vote.entity.VoteChoiceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteChoiceRepository extends JpaRepository<VoteChoice, VoteChoiceId> {
}
