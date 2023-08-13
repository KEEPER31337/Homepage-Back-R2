package com.keeper.homepage.domain.attendance.application;

import static com.keeper.homepage.global.error.ErrorCode.ATTENDANCE_ALREADY;
import static com.keeper.homepage.global.error.ErrorCode.ATTENDANCE_NOT_FOUND;

import com.keeper.homepage.domain.attendance.dao.AttendanceRepository;
import com.keeper.homepage.domain.attendance.dto.response.AttendanceInfoResponse;
import com.keeper.homepage.domain.attendance.dto.response.AttendancePointResponse;
import com.keeper.homepage.domain.attendance.dto.response.AttendanceRankResponse;
import com.keeper.homepage.domain.attendance.dto.response.AttendanceResponse;
import com.keeper.homepage.domain.attendance.entity.Attendance;
import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.point.application.PointLogService;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.redis.RedisUtil;
import com.keeper.homepage.global.util.web.WebUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
  private final PointLogService pointLogService;
  private final RedisUtil redisUtil;
  private final MemberFindService memberFindService;

  private static final long RANK_DATA_EXPIRE_DURATION = 60 * 60 * 24; // 60s * 60m * 24h로 하루를 의미함.

  private static final String ATTENDANCE_MESSAGE = "자동 출석입니다.";
  private static final int RANDOM_MIN_POINT = 100;
  private static final int RANDOM_MAX_POINT = 1000;
  public static final int DAILY_POINT = 1000;

  @Transactional
  public void create(Member member) {
    checkAlreadyAttendance(member);

    LocalDateTime now = LocalDateTime.now();
    int randomPoint = getRandomPoint();
    int rank = getTodayRank(now.toLocalDate());
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
    pointLogService.create(attendance);
  }

  private void checkAlreadyAttendance(Member member) {
    if (attendanceRepository.existsByMemberAndDate(member, LocalDate.now())) {
      throw new BusinessException(member.getId(), "memberId", ATTENDANCE_ALREADY);
    }
  }

  private int getRandomPoint() {
    return (int) (Math.random() * (RANDOM_MAX_POINT - RANDOM_MIN_POINT) + RANDOM_MIN_POINT);
  }

  private int getTodayRank(LocalDate now) {
    String key = "attendance:" + now.toString();
    return redisUtil.increaseAndGetWithExpire(key, RANK_DATA_EXPIRE_DURATION).intValue();
  }

  private int getRankPoint(int rank) {
    return RankPoint.getRankPoint(rank)
        .orElse(0);
  }

  private int getContinuousDay(Member member, LocalDate now) {
    Attendance yesterdayAttendance = attendanceRepository.findByMemberAndDate(member, now.minusDays(1))
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

  public Page<AttendanceRankResponse> getTodayRanks(Pageable pageable) {
    LocalDate now = LocalDate.now();
    Page<Attendance> attendances = attendanceRepository.findAllByDateOrderByRankAsc(now, pageable);
    return attendances.map(AttendanceRankResponse::from);
  }

  public List<AttendanceRankResponse> getContinuousRanks() {
    LocalDate now = LocalDate.now();
    List<Attendance> attendances = attendanceRepository.findAllByDateOrderByContinuousDayDesc(now);
    return attendances.stream()
        .limit(4)
        .map(AttendanceRankResponse::from)
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
    return attendanceRepository.findByMemberAndDate(member, now)
        .map(AttendanceInfoResponse::from)
        .orElseThrow(() -> new BusinessException(member.getId(), "memberId", ATTENDANCE_NOT_FOUND));
  }

  // TODO: 추후 프론트 라이브러리 요구사항에 맞게 수정이 필요
  public List<AttendanceResponse> getTotalAttendance(long memberId, LocalDate localDate) {
    Member member = memberFindService.findById(memberId);
    LocalDate lastDate = localDate.minusYears(1);
    return attendanceRepository.findAllRecent(member, lastDate)
        .stream()
        .map(AttendanceResponse::from)
        .toList();
  }
}
