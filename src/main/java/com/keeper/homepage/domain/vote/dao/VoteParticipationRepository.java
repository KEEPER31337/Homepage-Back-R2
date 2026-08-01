package com.keeper.homepage.domain.vote.dao;

import com.keeper.homepage.domain.vote.entity.VoteParticipation;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteParticipationRepository extends JpaRepository<VoteParticipation, UUID> {

  @Query("""
      SELECT participation.vote.id
      FROM VoteParticipation participation
      WHERE participation.member.id = :memberId
        AND participation.vote.id IN :voteIds
      """)
  Set<Long> findParticipatedVoteIds(
      @Param("memberId") long memberId,
      @Param("voteIds") Collection<Long> voteIds
  );
}
