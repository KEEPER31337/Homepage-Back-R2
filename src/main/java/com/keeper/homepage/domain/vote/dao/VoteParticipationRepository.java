package com.keeper.homepage.domain.vote.dao;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.vote.entity.Vote;
import com.keeper.homepage.domain.vote.entity.VoteParticipation;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteParticipationRepository extends JpaRepository<VoteParticipation, UUID> {

  boolean existsByVoteAndMember(Vote vote, Member member);

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

  @Modifying
  @Query("""
      UPDATE VoteParticipation participation
      SET participation.member = :virtualMember
      WHERE participation.member = :member
      """)
  void updateVirtualMember(
      @Param("member") Member member,
      @Param("virtualMember") Member virtualMember
  );
}
