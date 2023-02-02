package com.keeper.homepage.domain.auth.application;

import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.LoginId;
import com.keeper.homepage.domain.member.entity.embedded.Password;
import com.keeper.homepage.domain.member.entity.job.MemberHasMemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.error.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignInService {

  private final MemberRepository memberRepository;
  private final AuthCookieService authCookieService;

  @Transactional
  public void signIn(LoginId loginId, String rawPassword, HttpServletResponse response) {
    Member member = memberRepository.findByProfileLoginId(loginId)
        .orElseThrow(
            () -> new BusinessException(loginId.get(), "loginId", ErrorCode.MEMBER_NOT_FOUND));
    if (member.getProfile().getPassword().isWrongPassword(rawPassword)) {
      throw new BusinessException(loginId.get(), "loginId", ErrorCode.MEMBER_WRONG_ID_OR_PASSWORD);
    }
    authCookieService.setNewCookieInResponse(String.valueOf(member.getId()),
        getRoles(member), response);
  }

  private static String[] getRoles(Member member) {
    return member.getMemberJob()
        .stream()
        .map(MemberHasMemberJob::getMemberJob)
        .map(MemberJob::getType)
        .map(MemberJobType::name)
        .toArray(String[]::new);
  }
}
