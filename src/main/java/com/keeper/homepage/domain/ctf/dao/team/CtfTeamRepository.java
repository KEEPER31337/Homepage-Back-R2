package com.keeper.homepage.domain.ctf.dao.team;

import com.keeper.homepage.domain.ctf.entity.CtfContest;
import com.keeper.homepage.domain.ctf.entity.team.CtfTeam;
import com.keeper.homepage.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CtfTeamRepository extends JpaRepository<CtfTeam, Long> {

  Optional<CtfTeam> findByIdAndIdNot(long teamId, long virtualId);

  Page<CtfTeam> findAllByCtfContestAndNameIgnoreCaseContaining(CtfContest ctfContest, String search, Pageable pageable);

  @Modifying
  @Query("UPDATE CtfTeam c "
      + "SET c.creator = :virtualMember "
      + "WHERE c.creator = :member")
  void updateCreator(@Param("member") Member member, @Param("virtualMember") Member virtualMember);

}
