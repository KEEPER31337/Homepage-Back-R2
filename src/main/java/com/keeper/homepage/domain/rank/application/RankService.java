package com.keeper.homepage.domain.rank.application;

import com.keeper.homepage.domain.attendance.dao.AttendanceRepository;
import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.rank.dto.response.ContinuousAttendanceResponse;
import com.keeper.homepage.domain.rank.dto.response.PointRankResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankService {

  private static final long VIRTUAL_MEMBER_ID = 1;

  private final MemberRepository memberRepository;
  private final AttendanceRepository attendanceRepository;

  public Page<PointRankResponse> getPointRanking(Pageable pageable) {
    return memberRepository.findAllByIdIsNotOrderByPointDesc(VIRTUAL_MEMBER_ID, pageable)
        .map(PointRankResponse::from);
  }

  public List<ContinuousAttendanceResponse> getContinuousAttendance() {
    return attendanceRepository.findTop4DistinctByOrderByContinuousDayDesc().stream()
        .map(ContinuousAttendanceResponse::from)
        .toList();
  }
}
