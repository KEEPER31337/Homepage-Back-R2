package com.keeper.homepage.domain.ctf.dao.challenge;

import com.keeper.homepage.domain.ctf.entity.challenge.CtfChallenge;
import com.keeper.homepage.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CtfChallengeRepository extends JpaRepository<CtfChallenge, Long> {

  @Modifying
  @Query("UPDATE CtfChallenge c "
      + "SET c.creator = :virtualMember "
      + "WHERE c.creator = :member")
  void updateVirtualMember(@Param("member") Member member,
      @Param("virtualMember") Member virtualMember);

}
