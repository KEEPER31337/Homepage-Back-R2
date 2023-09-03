package com.keeper.homepage.domain.merit.dao;

import com.keeper.homepage.domain.merit.dto.response.MeritLogsGroupByMemberResponse;
import com.keeper.homepage.domain.merit.entity.MeritLog;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MeritLogRepository extends JpaRepository<MeritLog, Long> {

  Page<MeritLog> findAllByMemberId(Pageable pageable, Long memberId);

  Optional<MeritLog> findByMemberId(long memberId);

  long countByMemberId(long memberId);

  Page<MeritLog> findAllByTimeBetween(Pageable pageable, LocalDateTime startTime, LocalDateTime endTime);

  @Query("SELECT NEW com.keeper.homepage.domain.merit.dto.response.MeritLogsGroupByMemberResponse(m.memberId, m.memberRealName, m.memberGeneration, " +
      "CAST(SUM(CASE WHEN m.meritType.merit > 0 THEN m.meritType.merit ELSE 0 END) AS INTEGER), " +
      "CAST(SUM(CASE WHEN m.meritType.merit < 0 THEN m.meritType.merit ELSE 0 END) AS INTEGER)) " +
      "FROM MeritLog m GROUP BY m.memberId, m.memberRealName, m.memberGeneration")
  Page<MeritLogsGroupByMemberResponse> findAllTotalMeritLogs(Pageable pageable);
}
