package com.keeper.homepage.domain.point.application;

import static java.time.LocalDateTime.*;

import com.keeper.homepage.domain.attendance.entity.Attendance;
import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.point.dao.PointLogRepository;
import com.keeper.homepage.domain.point.entity.PointLog;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointLogService {

  private final PointLogRepository pointLogRepository;

  public Page<PointLog> findAllPointLogs(Pageable pageable, long memberId) {
    return pointLogRepository.findAllByMemberId(pageable, memberId);
  }
}
