package com.keeper.homepage.domain.seminar.dao;

import com.keeper.homepage.domain.seminar.entity.Seminar;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeminarRepository extends JpaRepository<Seminar, Long> {

  @Query("SELECT s FROM Seminar s WHERE DATE(s.openTime) =:date")
  Seminar findByOpenTime(@Param("date") LocalDate date);
}
