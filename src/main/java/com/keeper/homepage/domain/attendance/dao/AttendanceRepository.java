package com.keeper.homepage.domain.attendance.dao;

import com.keeper.homepage.domain.attendance.entity.Attendance;
import com.keeper.homepage.domain.member.entity.Member;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

  boolean existsByMemberAndDate(Member member, LocalDate date);

  Optional<Attendance> findByMemberAndDate(Member member, LocalDate date);

  Optional<Attendance> findTopByMemberOrderByDateDesc(Member member);

  Page<Attendance> findAllByDateOrderByRankAsc(LocalDate date, Pageable pageable);

  List<Attendance> findAllByDateOrderByContinuousDayDesc(LocalDate date);

  @Query("SELECT a FROM Attendance a "
      + "WHERE a.member = :member "
      + "AND a.date >= :localDate")
  List<Attendance> findAllRecent(@Param("member") Member member,
      @Param("localDate") LocalDate localDate);

}
