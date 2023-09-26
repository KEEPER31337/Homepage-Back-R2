package com.keeper.homepage.domain.ctf.dao;

import com.keeper.homepage.domain.ctf.entity.CtfContest;
import com.keeper.homepage.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CtfContestRepository extends JpaRepository<CtfContest, Long> {

  Optional<CtfContest> findByIdAndIdNotAndIsJoinableTrue(long contestId, long virtualId);

  Optional<CtfContest> findByIdAndIdNot(long contestId, long virtualId);

  Page<CtfContest> findAllByIdNot(long virtualId, Pageable pageable);

  @Modifying
  @Query("UPDATE CtfContest c "
      + "SET c.creator = :virtualMember "
      + "WHERE c.creator = :member")
  void updateCreator(@Param("member") Member member, @Param("virtualMember") Member virtualMember);

}
