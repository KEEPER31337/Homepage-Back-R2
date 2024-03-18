package com.keeper.homepage.domain.attendance.application;

import static com.keeper.homepage.global.error.ErrorCode.ATTENDANCE_NOT_FOUND;

import com.keeper.homepage.domain.attendance.dao.AttendanceRepository;
import com.keeper.homepage.domain.attendance.dto.response.AttendanceContinuousRankResponse;
import com.keeper.homepage.domain.attendance.dto.response.AttendanceInfoResponse;
import com.keeper.homepage.domain.attendance.dto.response.AttendancePointResponse;
import com.keeper.homepage.domain.attendance.dto.response.AttendanceResponse;
import com.keeper.homepage.domain.attendance.dto.response.AttendanceTodayRankResponse;
import com.keeper.homepage.domain.attendance.entity.Attendance;
import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.redis.RedisUtil;
import com.keeper.homepage.global.util.web.WebUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {

  private final AttendanceRepository attendanceRepository;
  private final RedisUtil redisUtil;
  private final MemberFindService memberFindService;

  private static final String ATTENDANCE_MESSAGE = "자동 출석입니다.";
  private static final String ATTENDANCE_POINT_MESSAGE = "출석 포인트";
  private static final int RANDOM_MIN_POINT = 100;
  private static final int RANDOM_MAX_POINT = 1000;
  public static final int DAILY_POINT = 1000;

  @Transactional
  public void create(long memberId) {
    Member member = memberFindService.findById(memberId);
    LocalDateTime now = LocalDateTime.now();
    if (attendanceRepository.existsByMemberAndDate(member, now.toLocalDate())) {
      return;
    }

    String rankKey = "attendance:" + now.toLocalDate();
    String memberKey = "attendance:member:" + memberId;

    int rank = redisUtil.increaseAndGetWithExpire(rankKey, RedisUtil.toMidNight()).intValue();
    redisUtil.setDataExpire(memberKey, rank, RedisUtil.toMidNight());

    int randomPoint = getRandomPoint();
    int rankPoint = getRankPoint(rank);
    int continuousDay = getContinuousDay(member, now.toLocalDate());
    int continuousPoint = getContinuousPoint(continuousDay);
    Attendance attendance = Attendance.builder()
        .time(now)
        .date(now.toLocalDate())
        .point(DAILY_POINT)
        .randomPoint(randomPoint)
        .rank(rank)
        .rankPoint(rankPoint)
        .continuousDay(continuousDay)
        .continuousPoint(continuousPoint)
        .ipAddress(WebUtil.getUserIP())
        .greetings(ATTENDANCE_MESSAGE)
        .member(member)
        .build();
    attendanceRepository.save(attendance);
    member.addPoint(attendance.getTotalPoint(), ATTENDANCE_POINT_MESSAGE);
  }

  private int getRandomPoint() {
    return (int) (Math.random() * (RANDOM_MAX_POINT - RANDOM_MIN_POINT) + RANDOM_MIN_POINT);
  }

  private int getRankPoint(int rank) {
    return RankPoint.getRankPoint(rank)
        .orElse(0);
  }

  private int getContinuousDay(Member member, LocalDate now) {
    Attendance yesterdayAttendance = attendanceRepository.findByMemberAndDate(member,
            now.minusDays(1))
        .orElse(null);
    if (yesterdayAttendance == null) {
      return 1;
    }
    return yesterdayAttendance.getContinuousDay() + 1;
  }

  private int getContinuousPoint(int continuousDay) {
    return BonusPoint.getBonusPoint(continuousDay)
        .orElse(0);
  }

  public Page<AttendanceTodayRankResponse> getTodayRanks(Pageable pageable) {
    LocalDate now = LocalDate.now();
    Page<Attendance> attendances = attendanceRepository.findAllByDateOrderByRankAsc(now, pageable);
    return attendances.map(AttendanceTodayRankResponse::from);
  }

  public List<AttendanceContinuousRankResponse> getContinuousRanks() {
    LocalDate now = LocalDate.now();
    List<Attendance> attendances = attendanceRepository.findAllByDateOrderByContinuousDayDesc(now);
    return attendances.stream()
        .limit(4)
        .map(AttendanceContinuousRankResponse::from)
        .toList();
  }

  public AttendancePointResponse getTodayAttendancePoint(Member member) {
    LocalDate now = LocalDate.now();
    return attendanceRepository.findByMemberAndDate(member, now)
        .map(AttendancePointResponse::from)
        .orElseThrow(() -> new BusinessException(member.getId(), "memberId", ATTENDANCE_NOT_FOUND));
  }

  public AttendanceInfoResponse getAttendanceInfo(long memberId) {
    Member member = memberFindService.findById(memberId);
    LocalDate now = LocalDate.now();
    Optional<Attendance> todayAttendance = attendanceRepository.findByMemberAndDate(member, now);
    if (todayAttendance.isPresent()) {
      return AttendanceInfoResponse.of(member, todayAttendance.get());
    }
    Attendance recentAttendance = attendanceRepository.findTopByMemberOrderByDateDesc(member)
        .orElse(Attendance.builder().build());
    return AttendanceInfoResponse.recent(member, recentAttendance);
  }

  public List<AttendanceResponse> getTotalAttendance(long memberId, LocalDate localDate) {
    Member member = memberFindService.findById(memberId);
    return attendanceRepository.findAllRecent(member, localDate)
        .stream()
        .map(AttendanceResponse::from)
        .toList();
  }
}
