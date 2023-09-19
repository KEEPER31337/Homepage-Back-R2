package com.keeper.homepage.domain.seminar.dao;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeminarAttendanceRepository extends JpaRepository<SeminarAttendance, Long> {

  boolean existsBySeminarAndMember(Seminar seminar, Member member);

  Optional<SeminarAttendance> findBySeminarAndMember(Seminar seminar, Member member);

  List<SeminarAttendance> findAllBySeminarAttendanceStatus(SeminarAttendanceStatus status);

  @Query("SELECT s FROM SeminarAttendance s "
      + "WHERE s.member.id = :memberId "
      + "AND DATE(s.attendTime) >= :localDate")
  List<SeminarAttendance> findAllRecentByMember(@Param("memberId") long memberId,
      @Param("localDate") LocalDate localDate);
}
