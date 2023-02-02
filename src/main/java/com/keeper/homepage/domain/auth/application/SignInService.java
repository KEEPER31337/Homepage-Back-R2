package com.keeper.homepage.domain.auth.application;

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
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignInService {

  private static final Random RANDOM = new Random();
  private static final int MAX_TEMPORARY_PASSWORD_LENGTH = 15;

  private final MemberRepository memberRepository;
  private final AuthCookieService authCookieService;
  private final MailUtil mailUtil;

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

  @Transactional(readOnly = true)
  public void findLoginId(EmailAddress email) {
    Member member = memberRepository.findByProfileEmailAddress(email)
        .orElseThrow(() -> new BusinessException(email.get(), "email", ErrorCode.MEMBER_NOT_FOUND));
    mailUtil.sendMail(List.of(email.get()), "KEEPER 로그인 아이디입니다.",
        "회원님의 로그인 아이디: " + member.getProfile().getLoginId().get());
  }

  @Transactional
  public void issueTemporaryPassword(EmailAddress email, LoginId loginId) {
    Member member = memberRepository.findByProfileEmailAddressAndProfileLoginId(email, loginId)
        .orElseThrow(() -> new BusinessException(email.get(), "email", ErrorCode.MEMBER_NOT_FOUND));

    String newPassword = generateRandomPassword();
    member.getProfile().changePassword(newPassword);
    mailUtil.sendMail(List.of(email.get()), "KEEPER 임시 비밀번호입니다.", "회원님의 임시 비밀번호: " + newPassword);
  }

  private static String generateRandomPassword() {
    char leftLimit = '0';
    char rightLimit = 'z';

    return RANDOM.ints(leftLimit, rightLimit + 1)
        .filter(i -> Character.isAlphabetic(i) || Character.isDigit(i))
        .limit(MAX_TEMPORARY_PASSWORD_LENGTH)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}
