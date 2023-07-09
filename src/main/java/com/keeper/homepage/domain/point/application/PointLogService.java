package com.keeper.homepage.domain.point.application;

import com.keeper.homepage.domain.attendance.entity.Attendance;
import com.keeper.homepage.domain.point.dao.PointLogRepository;
import com.keeper.homepage.domain.point.entity.PointLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointLogService {

  private final PointLogRepository pointLogRepository;

  private static final String ATTENDANCE_POINT_MESSAGE = "출석 포인트";

  @Transactional
  public void create(Attendance attendance) {
    pointLogRepository.save(PointLog.builder()
        .time(attendance.getTime())
        .member(attendance.getMember())
        .point(attendance.getTotalPoint())
        .detail(ATTENDANCE_POINT_MESSAGE)
        .isSpent(true)
        .build());
  }
}
