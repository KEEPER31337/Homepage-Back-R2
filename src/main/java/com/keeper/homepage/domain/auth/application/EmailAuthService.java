package com.keeper.homepage.domain.auth.application;

import com.keeper.homepage.domain.auth.dao.redis.EmailAuthRedisRepository;
import com.keeper.homepage.domain.auth.entity.redis.EmailAuthRedis;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.error.ErrorCode;
import com.keeper.homepage.global.util.mail.MailUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

  public static final int EMAIL_EXPIRED_SECONDS = 300; // 5분
  public static final int AUTH_CODE_LENGTH = 10;

  private static final Random RANDOM = new Random();

  private final MailUtil mailUtil;
  private final EmailAuthRedisRepository emailAuthRedisRepository;

  public int emailAuth(String email) {
    if (emailAuthRedisRepository.existsById(email)) {
      throw new BusinessException(email, "email", ErrorCode.TOO_MANY_REQUEST_AUTH_CODE);
    }

    String authCode = generateRandomAuthCode();
    setAuthCodeInRedis(email, authCode);
    sendKeeperAuthCodeMail(email, authCode);
    return EMAIL_EXPIRED_SECONDS;
  }

  private static String generateRandomAuthCode() {
    char leftLimit = '0';
    char rightLimit = 'z';

    return RANDOM.ints(leftLimit, rightLimit + 1)
        .filter(i -> Character.isAlphabetic(i) || Character.isDigit(i))
        .limit(EmailAuthService.AUTH_CODE_LENGTH)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }

  private void setAuthCodeInRedis(String email, String authCode) {
    emailAuthRedisRepository.save(EmailAuthRedis.of(email, authCode));
  }

  void sendKeeperAuthCodeMail(String email, String authCode) {
    List<String> toUserList = new ArrayList<>(List.of(email));
    String subject = "KEEPER 인증코드 발송 메일입니다.";
    String text = "KEEPER 인증코드는 " + authCode + " 입니다.";
    mailUtil.sendMail(toUserList, subject, text);
  }
}
