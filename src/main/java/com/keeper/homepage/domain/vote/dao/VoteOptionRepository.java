package com.keeper.homepage.domain.vote.dao;

import com.keeper.homepage.domain.vote.entity.VoteOption;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {

  List<VoteOption> findAllByAgendaIdInOrderByDisplayOrderAsc(Collection<Long> agendaIds);

  @Query("""
      SELECT option
      FROM VoteOption option
      JOIN FETCH option.agenda
      WHERE option.id IN :optionIds
      """)
  List<VoteOption> findAllWithAgendaByIdIn(@Param("optionIds") Collection<Long> optionIds);
}
