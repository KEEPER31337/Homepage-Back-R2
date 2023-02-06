package com.keeper.homepage.domain.member.application;

import com.keeper.homepage.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

  @Transactional
  public void changePassword(Member me, String newPassword) {
    me.getProfile().changePassword(newPassword);
  }
}
