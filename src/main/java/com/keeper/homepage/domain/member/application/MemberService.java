package com.keeper.homepage.domain.member.application;

import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.dto.response.MemberResponse;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.RealName;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

  public List<MemberResponse> getMembers(String searchName) {
    if (searchName == null) {
      return memberFindService.findAll()
          .map(MemberResponse::from)
          .toList();
    }
    return memberFindService.findAllByRealName(RealName.from(searchName))
        .map(MemberResponse::from)
        .toList();
  }
}
