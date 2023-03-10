package com.keeper.homepage.domain.seminar.dao;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarAttendanceRepository extends JpaRepository<SeminarAttendance, Long> {

  boolean existsBySeminarAndMember(Seminar seminar, Member member);
  Optional<SeminarAttendance> findBySeminarAndMember(Seminar seminar, Member member);
}
