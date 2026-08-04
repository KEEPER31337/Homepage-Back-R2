package com.keeper.homepage.domain.vote.dao;

import com.keeper.homepage.domain.vote.entity.Vote;
import com.keeper.homepage.domain.vote.entity.VoteReceipt;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteReceiptRepository extends JpaRepository<VoteReceipt, UUID> {

  @Query("""
      SELECT DISTINCT receipt
      FROM VoteReceipt receipt
      LEFT JOIN FETCH receipt.choices choice
      LEFT JOIN FETCH choice.option option
      LEFT JOIN FETCH option.agenda
      WHERE receipt.vote = :vote
      """)
  List<VoteReceipt> findAllWithChoicesByVote(@Param("vote") Vote vote);
}
