package com.keeper.homepage.domain.election.dao;

import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ElectionRepository extends JpaRepository<Election, Long> {

  Optional<Election> findByIdAndIdNot(Long electionId, Long virtualId);

  Page<Election> findAllByIdNot(long virtualId, Pageable pageable);

  @Modifying
  @Query("UPDATE Election e "
      + "SET e.member = :virtualMember "
      + "WHERE e.member = :member")
  void updateVirtualMember(@Param("member") Member member, @Param("virtualMember") Member virtualMember);

}
