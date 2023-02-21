package com.keeper.homepage.domain.seminar.dao;

import com.keeper.homepage.domain.seminar.entity.Seminar;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeminarRepository extends JpaRepository<Seminar, Long> {

  List<Seminar> findAllByIdIsNot(Long seminarId);

  @Query("SELECT s FROM Seminar s WHERE DATE(s.openTime) =:localDate")
  Optional<Seminar> findByOpenTime(@Param("localDate") LocalDate localDate);
}
