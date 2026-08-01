package com.keeper.homepage.domain.vote.dao;

import com.keeper.homepage.domain.vote.entity.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {

}
