package com.keeper.homepage.domain.point.application;

import static com.keeper.homepage.global.error.ErrorCode.POINT_UPDATE_FAILED;

import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.point.dao.PointLogRepository;
import com.keeper.homepage.domain.point.entity.PointLog;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.error.ErrorCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

  private static final int MAX_POINT = Integer.MAX_VALUE;

  private final MemberRepository memberRepository;
  private final PointLogRepository pointLogRepository;

  @Transactional
  public void changePointByDelta(long memberId, int delta, String message) {
    changePointByDelta(memberId, delta, message, POINT_UPDATE_FAILED);
  }

  @Transactional
  public void changePointByDelta(long memberId, int delta, String message, ErrorCode errorCode) {
    if (delta == Integer.MIN_VALUE) {
      throw new BusinessException(delta, "point", errorCode);
    }
    int updatedCount = memberRepository.updatePointByDelta(memberId, delta, MAX_POINT);
    if (updatedCount != 1) {
      throw new BusinessException(delta, "point", errorCode);
    }
    pointLogRepository.save(PointLog.builder()
        .time(LocalDateTime.now())
        .member(memberRepository.getReferenceById(memberId))
        .point(delta)
        .detail(message)
        .build());
  }
}
