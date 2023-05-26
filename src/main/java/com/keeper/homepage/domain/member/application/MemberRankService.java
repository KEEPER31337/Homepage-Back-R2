package com.keeper.homepage.domain.member.application;

import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.dto.response.MemberPointRankResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberRankService {

  private static final long VIRTUAL_MEMBER_ID = 1;
  private final MemberRepository memberRepository;

  public Page<MemberPointRankResponse> getPointRanking(Pageable pageable) {
    return memberRepository.findAllByIdIsNotOrderByPointDesc(VIRTUAL_MEMBER_ID, pageable)
        .map(MemberPointRankResponse::from);
  }
}
