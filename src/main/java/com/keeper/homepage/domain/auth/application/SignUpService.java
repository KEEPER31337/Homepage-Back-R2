package com.keeper.homepage.domain.auth.application;

import static com.keeper.homepage.global.error.ErrorCode.AUTH_CODE_EXPIRED;
import static com.keeper.homepage.global.error.ErrorCode.AUTH_CODE_MISMATCH;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_EMAIL_DUPLICATE;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_LOGIN_ID_DUPLICATE;
import static com.keeper.homepage.global.error.ErrorCode.MEMBER_STUDENT_ID_DUPLICATE;

import com.keeper.homepage.domain.auth.dao.redis.EmailAuthRedisRepository;
import com.keeper.homepage.domain.auth.dto.request.SignUpRequest;
import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.error.BusinessException;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpService {

  private final MemberRepository memberRepository;
  private final EmailAuthRedisRepository emailAuthRedisRepository;
  private final CheckDuplicateService checkDuplicateService;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public long signUp(SignUpRequest request) {
    checkIsDuplicateEmail(request.getEmail());
    checkIsDuplicateLoginId(request.getLoginId());
    checkIsDuplicateStudentId(request.getStudentId());

    String actualAuthCode = getActualAuthCode(request.getEmail());
    checkAuthCodeMatch(request.getAuthCode(), actualAuthCode);
    return memberRepository.save(Member.builder()
            .profile(request.toMemberProfile(passwordEncoder))
            .build())
        .getId();
  }

  private void checkIsDuplicateEmail(String email) {
    if (checkDuplicateService.isDuplicateEmail(email)) {
      throw new BusinessException(email, "email", MEMBER_EMAIL_DUPLICATE);
    }
  }

  private void checkIsDuplicateLoginId(String loginId) {
    if (checkDuplicateService.isDuplicateLoginId(loginId)) {
      throw new BusinessException(loginId, "loginId", MEMBER_LOGIN_ID_DUPLICATE);
    }
  }

  private void checkIsDuplicateStudentId(String studentId) {
    if (checkDuplicateService.isDuplicateStudentID(studentId)) {
      throw new BusinessException(studentId, "studentId", MEMBER_STUDENT_ID_DUPLICATE);
    }
  }

  void checkAuthCodeMatch(String requestAuthCode, String actualAuthCode) {
    if (!actualAuthCode.equals(requestAuthCode)) {
      throw new BusinessException(requestAuthCode, "authCode", AUTH_CODE_MISMATCH);
    }
  }

  String getActualAuthCode(@Email String email) {
    return emailAuthRedisRepository.findById(email)
        .orElseThrow(() -> new BusinessException(email, "email", AUTH_CODE_EXPIRED))
        .getAuthCode();
  }
}
