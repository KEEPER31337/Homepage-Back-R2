package com.keeper.homepage.domain.vote.dao;

import com.keeper.homepage.domain.vote.entity.VoteAgenda;
import com.keeper.homepage.domain.vote.entity.Vote;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteAgendaRepository extends JpaRepository<VoteAgenda, Long> {

  List<VoteAgenda> findAllByVoteOrderByDisplayOrderAsc(Vote vote);
}
