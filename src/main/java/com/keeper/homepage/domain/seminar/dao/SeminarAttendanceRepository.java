package com.keeper.homepage.domain.seminar.dao;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface SeminarAttendanceRepository extends JpaRepository<SeminarAttendance, Long> {

  boolean existsBySeminarAndMember(@Param("seminar") Seminar seminar, @Param("member") Member member);
}
