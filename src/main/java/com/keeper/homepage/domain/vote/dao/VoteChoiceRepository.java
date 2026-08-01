package com.keeper.homepage.domain.vote.dao;

import com.keeper.homepage.domain.vote.entity.VoteChoice;
import com.keeper.homepage.domain.vote.entity.VoteChoiceId;
import com.keeper.homepage.domain.vote.entity.VoteReceipt;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteChoiceRepository extends JpaRepository<VoteChoice, VoteChoiceId> {

  @Query("""
      SELECT choice
      FROM VoteChoice choice
      JOIN FETCH choice.option option
      JOIN FETCH option.agenda agenda
      WHERE choice.receipt = :receipt
      ORDER BY agenda.displayOrder, option.displayOrder
      """)
  List<VoteChoice> findAllWithOptionAndAgendaByReceipt(
      @Param("receipt") VoteReceipt receipt
  );
}
