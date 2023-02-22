package com.keeper.homepage.domain.seminar.dao;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeminarAttendanceRepository extends JpaRepository<SeminarAttendance, Long> {

  @Query("SELECT count(sa) FROM SeminarAttendance sa "
      + "WHERE sa.seminar = :seminar "
      + "AND sa.member = :member ")
  long existsByAttendance(@Param("seminar") Seminar seminar, @Param("member") Member member);
}
