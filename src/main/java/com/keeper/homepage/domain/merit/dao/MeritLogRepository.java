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

  @Query(
      value = "SELECT NEW com.keeper.homepage.domain.merit.dto.response.MeritLogsGroupByMemberResponse(m.memberId, mem.profile.realName.realName, mem.generation.generation, " +
          "CAST(SUM(CASE WHEN m.meritType.isMerit = true THEN m.meritType.merit ELSE 0 END) AS INTEGER), " +
          "CAST(SUM(CASE WHEN m.meritType.isMerit = false THEN m.meritType.merit ELSE 0 END) AS INTEGER)) " +
          "FROM MeritLog m JOIN Member mem ON mem.id = m.memberId " +
          "WHERE mem.id <> 1 " + // virtual member 제외
          "GROUP BY m.memberId, mem.profile.realName.realName, mem.generation.generation " +
          "ORDER BY m.memberId",
      countQuery = "SELECT COUNT(DISTINCT m.memberId) FROM MeritLog m JOIN Member mem ON mem.id = m.memberId " +
          "WHERE mem.id <> 1")
  Page<MeritLogsGroupByMemberResponse> findAllTotalMeritLogs(Pageable pageable);

  @Query("SELECT m FROM MeritLog m WHERE m.meritType.isMerit = true")
  Page<MeritLog> findMeritLogs(Pageable pageable);

  @Query("SELECT m FROM MeritLog m WHERE m.meritType.isMerit = false")
  Page<MeritLog> findDeMeritLogs(Pageable pageable);
}
