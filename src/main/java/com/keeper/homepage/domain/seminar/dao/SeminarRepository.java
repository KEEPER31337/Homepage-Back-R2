package com.keeper.homepage.domain.seminar.dao;

import com.keeper.homepage.domain.seminar.entity.Seminar;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeminarRepository extends JpaRepository<Seminar, Long> {

  // TODO: 2023-02-13 List로 받아서 러프하게 할지, 하나만 받을지 고민해보기 
  Optional<Seminar> findByOpenTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
