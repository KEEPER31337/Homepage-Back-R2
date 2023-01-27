package com.keeper.homepage.domain.auth.application;

import static com.keeper.homepage.global.error.ErrorCode.AUTH_CODE_EXPIRED;
import static com.keeper.homepage.global.error.ErrorCode.AUTH_CODE_MISMATCH;

import com.keeper.homepage.domain.auth.dao.redis.EmailAuthRedisRepository;
import com.keeper.homepage.domain.auth.dto.request.SignUpRequest;
import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.error.BusinessException;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpService {

  private final MemberRepository memberRepository;
  private final EmailAuthRedisRepository emailAuthRedisRepository;

  @Transactional
  public long signUp(SignUpRequest request) {
    String actualAuthCode = getActualAuthCode(request.getEmail());
    checkAuthCodeMatch(request.getAuthCode(), actualAuthCode);
    return memberRepository.save(Member.builder()
            .profile(request.toMemberProfile())
            .build())
        .getId();
  }

  private static void checkAuthCodeMatch(String requestAuthCode, String actualAuthCode) {
    if (!actualAuthCode.equals(requestAuthCode)) {
      throw new BusinessException(requestAuthCode, "authCode", AUTH_CODE_MISMATCH);
    }
  }

  private String getActualAuthCode(@Email String email) {
    return emailAuthRedisRepository.findById(email)
        .orElseThrow(() -> new BusinessException(email, "email", AUTH_CODE_EXPIRED))
        .getAuthCode();
  }
}
