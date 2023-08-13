package com.keeper.homepage.domain.point.application;

import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.point.dao.PointLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GivePointService {

  private final MemberFindService memberFindService;
  private final PointLogService pointLogService;

  @Transactional
  public void givePoint(long giverId, long receiverId, int point, String message) {

    Member giver = memberFindService.findById(giverId);
    Member receiver = memberFindService.findById(receiverId);

    giver.minusPoint(point);
    receiver.plusPoint(point);

    pointLogService.create(giver, receiver, -point, message);
    pointLogService.create(receiver, null, point, message);


  }

}
