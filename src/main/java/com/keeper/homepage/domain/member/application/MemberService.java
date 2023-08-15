package com.keeper.homepage.domain.member.application;

import static com.keeper.homepage.domain.member.application.convenience.MemberFindService.VIRTUAL_MEMBER_ID;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_CANNOT_FOLLOW_ME;

import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.dto.response.MemberPointRankResponse;
import com.keeper.homepage.domain.member.dto.response.MemberResponse;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.RealName;
import com.keeper.homepage.global.error.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

  private final MemberRepository memberRepository;
  private final MemberFindService memberFindService;

  @Transactional
  public void changePassword(Member me, String newPassword) {
    me.getProfile().changePassword(newPassword);
  }

  public List<MemberResponse> getMembersByRealName(String searchName) {
    if (searchName == null) {
      return memberFindService.findAll()
          .map(MemberResponse::from)
          .toList();
    }
    return memberFindService.findAllByRealName(RealName.from(searchName))
        .map(MemberResponse::from)
        .toList();
  }

  public Page<MemberPointRankResponse> getPointRanking(Pageable pageable) {
    return memberRepository.findAllByIdIsNotOrderByPointDesc(VIRTUAL_MEMBER_ID, pageable)
        .map(MemberPointRankResponse::from);
  }

  @Transactional
  public void follow(Member member, long otherId) {
    Member other = memberFindService.findById(otherId);
    if (other.equals(member)) {
      throw new BusinessException(member.getId(), "memberId", MEMBER_CANNOT_FOLLOW_ME);
    }
    member.follow(other);
  }

  @Transactional
  public void unfollow(Member member, long otherId) {
    Member other = memberFindService.findById(otherId);
    member.unfollow(other);
  }
}
