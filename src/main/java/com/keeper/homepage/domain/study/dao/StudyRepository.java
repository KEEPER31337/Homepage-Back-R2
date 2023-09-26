package com.keeper.homepage.domain.study.dao;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.study.entity.Study;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyRepository extends JpaRepository<Study, Long> {

  List<Study> findAllByYearAndSeason(int year, int season);

  @Modifying
  @Query("UPDATE Study s "
      + "SET s.headMember = :virtualMember "
      + "WHERE s.headMember = :member")
  void updateMember(@Param("member") Member member, @Param("virtualMember") Member virtualMember);

}
