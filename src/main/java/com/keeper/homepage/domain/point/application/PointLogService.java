package com.keeper.homepage.domain.point.application;

import com.keeper.homepage.domain.attendance.entity.Attendance;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.point.dao.PointLogRepository;
import com.keeper.homepage.domain.point.entity.PointLog;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointLogService {

  private final PointLogRepository pointLogRepository;

  private static final String ATTENDANCE_POINT_MESSAGE = "출석 포인트";
  private static final String EXAM_READ_POINT_MESSAGE = "족보 열람";

  @Transactional
  public void createAttendanceLog(Attendance attendance) {
    pointLogRepository.save(PointLog.builder()
        .time(attendance.getTime())
        .member(attendance.getMember())
        .point(attendance.getTotalPoint())
        .detail(ATTENDANCE_POINT_MESSAGE)
        .isSpent(false)
        .build());
  }

  @Transactional
  public void createExamLog(Member member, int point) {
    pointLogRepository.save(PointLog.builder()
        .time(LocalDateTime.now())
        .member(member)
        .point(-point)
        .detail(EXAM_READ_POINT_MESSAGE)
        .isSpent(false)
        .build());
  }
}
