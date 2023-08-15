package com.keeper.homepage.domain.merit.dao;

import com.keeper.homepage.domain.merit.entity.MeritLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeritLogRepository extends JpaRepository<MeritLog, Long> {

  Page<MeritLog> findAllByMemberId(Pageable pageable, Long memberId);
}
