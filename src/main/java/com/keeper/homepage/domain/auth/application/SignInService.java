package com.keeper.homepage.domain.auth.application;

import com.keeper.homepage.domain.auth.dto.response.SignInResponse;
import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.EmailAddress;
import com.keeper.homepage.domain.member.entity.embedded.LoginId;
import com.keeper.homepage.domain.member.entity.job.MemberHasMemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.error.ErrorCode;
import com.keeper.homepage.global.util.mail.MailUtil;
import com.keeper.homepage.global.util.redis.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignInService {

  private static final Random RANDOM = new Random();
  private static final String PASSWORD_AUTH_CODE_KEY = "PW_AUTH_";
  private static final int MAX_PASSWORD_AUTH_CODE_LENGTH = 8;
  private static final int AUTH_CODE_EXPIRE_MILLIS = 5 * 60 * 1000; // 5분

  private final MemberRepository memberRepository;
  private final AuthCookieService authCookieService;
  private final RedisUtil redisUtil;
  private final MailUtil mailUtil;

  @Transactional
  public SignInResponse signIn(LoginId loginId, String rawPassword, HttpServletRequest request,
      HttpServletResponse response) {
    Member member = memberRepository.findByProfileLoginId(loginId)
        .orElseThrow(
            () -> new BusinessException(loginId.get(), "loginId", ErrorCode.MEMBER_NOT_FOUND));
    if (member.getProfile().getPassword().isWrongPassword(rawPassword)) {
      throw new BusinessException(loginId.get(), "loginId", ErrorCode.MEMBER_WRONG_ID_OR_PASSWORD);
    }
    authCookieService.setNewCookieInResponse(String.valueOf(member.getId()),
        getRoles(member), request.getHeader(HttpHeaders.USER_AGENT), response);
    return SignInResponse.of(member, Arrays.stream(getRoles(member)).toList());
  }

  private static String[] getRoles(Member member) {
    return member.getMemberJob()
        .stream()
        .map(MemberHasMemberJob::getMemberJob)
        .map(MemberJob::getType)
        .map(MemberJobType::name)
        .toArray(String[]::new);
  }

  @Transactional(readOnly = true)
  public void findLoginId(EmailAddress email) {
    Member member = memberRepository.findByProfileEmailAddress(email)
        .orElseThrow(() -> new BusinessException(email.get(), "email", ErrorCode.MEMBER_NOT_FOUND));
    mailUtil.sendMail(List.of(email.get()), "KEEPER 로그인 아이디입니다.",
        "회원님의 로그인 아이디: " + member.getProfile().getLoginId().get());
  }

  public int sendPasswordChangeAuthCode(EmailAddress email, LoginId loginId) {
    Member member = memberRepository.findByProfileEmailAddressAndProfileLoginId(email, loginId)
        .orElseThrow(() -> new BusinessException(email.get(), "email", ErrorCode.MEMBER_NOT_FOUND));

    String authCode = generateRandomAuthCode();
    redisUtil.setDataExpire(PASSWORD_AUTH_CODE_KEY + member.getId(), authCode,
        AUTH_CODE_EXPIRE_MILLIS);
    mailUtil.sendMail(List.of(email.get()), "KEEPER 비밀번호 변경 인증 코드입니다.",
        "회원님의 비밀번호 변경 인증 코드: " + authCode);

    return AUTH_CODE_EXPIRE_MILLIS / 1000;
  }

  private static String generateRandomAuthCode() {
    char leftLimit = '0';
    char rightLimit = 'z';

    return RANDOM.ints(leftLimit, rightLimit + 1)
        .filter(i -> Character.isAlphabetic(i) || Character.isDigit(i))
        .limit(MAX_PASSWORD_AUTH_CODE_LENGTH)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }

  public boolean isAuthenticated(EmailAddress email, LoginId loginId, String requestAuthCode) {
    Member member = memberRepository.findByProfileEmailAddressAndProfileLoginId(email, loginId)
        .orElseThrow(() -> new BusinessException(email.get(), "email", ErrorCode.MEMBER_NOT_FOUND));

    Optional<String> data = redisUtil.getData(PASSWORD_AUTH_CODE_KEY + member.getId(),
        String.class);
    return data.map(correctAuthCode -> correctAuthCode.equals(requestAuthCode)).orElse(false);
  }

  @Transactional
  public void changePassword(String authCode, LoginId loginId, EmailAddress email,
      String rawPassword) {
    if (!isAuthenticated(email, loginId, authCode)) {
      throw new BusinessException(authCode, "authCode", ErrorCode.AUTH_CODE_MISMATCH);
    }
    Member member = memberRepository.findByProfileEmailAddressAndProfileLoginId(email, loginId)
        .orElseThrow(() -> new BusinessException(email.get(), "email", ErrorCode.MEMBER_NOT_FOUND));
    member.getProfile().changePassword(rawPassword);
  }
}
