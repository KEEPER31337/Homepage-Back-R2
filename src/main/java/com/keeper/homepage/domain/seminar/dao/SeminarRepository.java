package com.keeper.homepage.domain.seminar.dao;

import com.keeper.homepage.domain.seminar.entity.Seminar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeminarRepository extends JpaRepository<Seminar, Long> {

  List<Seminar> findAllByIdIsNot(Long seminarId);

  @Query("SELECT s FROM Seminar s WHERE DATE(s.openTime) =:localDate")
  Optional<Seminar> findByOpenTime(@Param("localDate") LocalDate localDate);

  @Query("SELECT s FROM Seminar s "
      + "WHERE s.attendanceCloseTime is not null "
      + "AND s.latenessCloseTime is not null "
      + "AND s.latenessCloseTime > :dateTime")
  Optional<Seminar> findByAvailable(@Param("dateTime") LocalDateTime dateTime);

  @Query("SELECT s FROM Seminar s "
      + "WHERE s.id <> 1 " // virtual seminar data 제외
      + "AND DATE(s.openTime) < :localDate "
      + "AND DATE(s.latenessCloseTime) < :localDate "
      + "ORDER BY s.openTime DESC "
      + "LIMIT 1")
  Optional<Seminar> findRecentlyDoneSeminar(@Param("localDate") LocalDate localDate);
}
