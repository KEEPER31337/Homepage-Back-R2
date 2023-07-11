package com.keeper.homepage.domain.point.dao;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.point.entity.PointLog;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointLogRepository extends JpaRepository<PointLog, Long> {

  Optional<PointLog> findByMember(Member member);
}
