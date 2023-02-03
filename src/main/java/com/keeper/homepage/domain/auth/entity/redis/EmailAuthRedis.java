package com.keeper.homepage.domain.auth.entity.redis;

import static com.keeper.homepage.domain.auth.application.EmailAuthService.EMAIL_EXPIRED_SECONDS;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "emailAuth", timeToLive = EMAIL_EXPIRED_SECONDS)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailAuthRedis {

  @Id
  private final String email;
  private final String authCode;

  public static EmailAuthRedis of(String email, String authCode) {
    return new EmailAuthRedis(email, authCode);
  }
}
