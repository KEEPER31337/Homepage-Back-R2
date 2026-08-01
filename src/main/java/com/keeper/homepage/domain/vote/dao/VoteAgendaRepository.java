package com.keeper.homepage.domain.vote.dao;

import com.keeper.homepage.domain.vote.entity.VoteAgenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteAgendaRepository extends JpaRepository<VoteAgenda, Long> {

}
