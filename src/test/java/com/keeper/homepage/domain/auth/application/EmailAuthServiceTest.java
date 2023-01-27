package com.keeper.homepage.domain.auth.application;

import static com.keeper.homepage.domain.auth.application.EmailAuthService.EMAIL_EXPIRED_SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

import com.keeper.homepage.IntegrationTest;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmailAuthServiceTest extends IntegrationTest {

  @Test
  @DisplayName("알파벳과 숫자로 이루어진 인증 코드가 잘 생성되어야 하고, Redis에 해당 내용이 들어있어야 한다.")
  void should_generateSuccessfullyAndInRedis() {
    doNothing().when(emailAuthService).sendKeeperAuthCodeMail(anyString(), anyString());
    String validEmail = String.format("%s@%s.%s", getRandomUUIDLengthWith(5),
        getRandomUUIDLengthWith(5), getRandomUUIDLengthWith(3));
    assertThat(emailAuthRedisRepository.findById(validEmail)).isEmpty();

    int result = emailAuthService.emailAuth(validEmail);

    assertThat(result).isEqualTo(EMAIL_EXPIRED_SECONDS);
    assertThat(emailAuthRedisRepository.findById(validEmail)).isNotEmpty();
    String authCode = emailAuthRedisRepository.findById(validEmail).orElseThrow().getAuthCode();
    assertThat(isEveryCharacterAlphabeticOrDigit(authCode)).isTrue();
  }

  private static boolean isEveryCharacterAlphabeticOrDigit(String authCode) {
    return authCode.chars()
        .allMatch(c -> Character.isAlphabetic(c) || Character.isDigit(c));
  }

  private static String getRandomUUIDLengthWith(int length) {
    String randomString = UUID.randomUUID()
        .toString();
    length = Math.min(length, randomString.length());
    return randomString.substring(0, length);
  }
}
