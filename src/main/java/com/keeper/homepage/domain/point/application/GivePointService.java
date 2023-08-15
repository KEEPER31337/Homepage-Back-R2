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
  private static final String GIVERDEFAULTMESSAGE = "포인트 선물 보내기";
  private static final String RECEIVERDEFAULTMESSAGE = "포인트 선물 흭득";

  @Transactional
  public void presentPoint(long giverId, long receiverId, int point, String message) {

    Member giver = memberFindService.findById(giverId);
    Member receiver = memberFindService.findById(receiverId);

    String giverMessage = GIVERDEFAULTMESSAGE + (message != null ? " - " + message : "");
    String receiverMessage = RECEIVERDEFAULTMESSAGE + (message != null ? " - " + message : "");

    giver.minusPoint(point, giverMessage);
    receiver.addPoint(point, receiverMessage);
  }
}
