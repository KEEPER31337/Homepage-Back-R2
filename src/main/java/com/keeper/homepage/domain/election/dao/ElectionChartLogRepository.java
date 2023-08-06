package com.keeper.homepage.domain.election.dao;

import com.keeper.homepage.domain.election.entity.ElectionChartLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionChartLogRepository extends JpaRepository<ElectionChartLog, Long> {

}
