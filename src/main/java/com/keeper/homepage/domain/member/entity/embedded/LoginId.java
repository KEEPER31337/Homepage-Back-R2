package com.keeper.homepage.domain.member.entity.embedded;

import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"loginId"})
public class LoginId {

  public static final int MAX_LOGIN_ID_LENGTH = 80;
  public static final String LOGIN_ID_INVALID = "로그인 아이디는 4~12자 영어, 숫자, '_'만 가능합니다.";
  public static final String LOGIN_ID_REGEX = "^[a-zA-Z0-9_]{4,12}";

  private static final Pattern LOGIN_ID_FORMAT = Pattern.compile(LOGIN_ID_REGEX);

  private String loginId;

  public static LoginId from(String loginId) {
    if (isInvalidFormat(loginId)) {
      throw new IllegalArgumentException(LOGIN_ID_INVALID);
    }
    return new LoginId(loginId);
  }

  private static boolean isInvalidFormat(String loginId) {
    return !LOGIN_ID_FORMAT.matcher(loginId).find();
  }

  public String get() {
    return this.loginId;
  }
}
