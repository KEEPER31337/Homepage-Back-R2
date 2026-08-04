package com.keeper.homepage.domain.vote.dao;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.vote.entity.Vote;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<Vote, Long> {

  List<Vote> findAllByOrderByStartAtDescIdDesc();

  List<Vote> findAllByStartAtGreaterThanEqualAndStartAtLessThanOrderByStartAtDescIdDesc(
      LocalDateTime startAt,
      LocalDateTime endAt
  );

  @Modifying
  @Query("""
      UPDATE Vote vote
      SET vote.createdBy = :virtualMember
      WHERE vote.createdBy = :member
      """)
  void updateVirtualMember(
      @Param("member") Member member,
      @Param("virtualMember") Member virtualMember
  );
}
