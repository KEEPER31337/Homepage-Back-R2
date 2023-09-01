package com.keeper.homepage.domain.merit.dao;

import com.keeper.homepage.domain.merit.entity.MeritLog;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeritLogRepository extends JpaRepository<MeritLog, Long> {

  Page<MeritLog> findAllByMemberId(Pageable pageable, Long memberId);

  Optional<MeritLog> findByMemberId(long memberId);

  long countByMemberId(long memberId);

  Page<MeritLog> findAllByTimeBetween(Pageable pageable, LocalDateTime startTime, LocalDateTime endTime);
}
