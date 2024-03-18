package com.keeper.homepage.domain.seminar.dao;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeminarRepository extends JpaRepository<Seminar, Long> {

  List<Seminar> findAllByIdIsNot(Long seminarId, Sort sort);

  Optional<Seminar> findByIdAndIdNot(Long seminarId, Long virtualId);

  @Query("SELECT s FROM Seminar s WHERE DATE(s.openTime) =:localDate")
  Optional<Seminar> findByOpenTime(@Param("localDate") LocalDate localDate);

  @Query("SELECT s FROM Seminar s "
      + "WHERE s.attendanceCloseTime is not null "
      + "AND s.latenessCloseTime is not null "
      + "AND s.latenessCloseTime > :dateTime "
      + "AND DATE(s.openTime) >= :localDate "
      + "ORDER BY s.openTime ASC "
      + "LIMIT 1")
  Optional<Seminar> findByAvailable(@Param("dateTime") LocalDateTime dateTime,
      @Param("localDate") LocalDate localDate);

  @Query("SELECT s FROM Seminar s "
      + "WHERE s.id <> 1 " // virtual seminar data 제외
      + "AND DATE(s.openTime) < :localDate "
      + "ORDER BY s.openTime DESC "
      + "LIMIT 1")
  Optional<Seminar> findRecentlyDoneSeminar(@Param("localDate") LocalDate localDate);

  @Query("SELECT s FROM Seminar s "
      + "WHERE s.id <> 1 " // virtual seminar data 제외"
      + "AND DATE(s.openTime) >= :localDate "
      + "ORDER BY s.openTime ASC "
      + "LIMIT 2")
  List<Seminar> findRecentlyUpcomingSeminar(@Param("localDate") LocalDate localDate);

  @Query("SELECT COUNT(s) > 0 "
      + "FROM Seminar s WHERE DATE(s.openTime) = :localDate")
  boolean existsByOpenTime(@Param("localDate") LocalDate localDate);

  @Modifying
  @Query("UPDATE Seminar s "
      + "SET s.starter = :virtualMember "
      + "WHERE s.starter = :member")
  void updateVirtualMember(@Param("member") Member member,
      @Param("virtualMember") Member virtualMember);

  @Query("SELECT s FROM Seminar s "
      + "WHERE DATE(s.openTime) >= :localDate "
      + "ORDER BY s.openTime DESC")
  List<Seminar> findAllRecent(@Param("localDate") LocalDate localDate);

}
