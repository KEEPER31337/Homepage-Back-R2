package com.keeper.homepage.domain.member.application.convenience;

import static com.keeper.homepage.global.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.STUDY_NOT_FOUND;

import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.study.entity.Study;
import com.keeper.homepage.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberFindService {

  private static final long VIRTUAL_MEMBER_ID = 1;
  private final MemberRepository memberRepository;

  public Member getVirtualMember() {
    return memberRepository.findById(VIRTUAL_MEMBER_ID)
        .orElseThrow(() -> new BusinessException(VIRTUAL_MEMBER_ID, "memberId", MEMBER_NOT_FOUND));
  }

  public Member findById(long memberId) {
    return memberRepository.findByIdAndIdNot(memberId, VIRTUAL_MEMBER_ID)
        .orElseThrow(() -> new BusinessException(memberId, "memberId", MEMBER_NOT_FOUND));
  }
}
