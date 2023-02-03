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
@EqualsAndHashCode(of = {"nickName"})
public class Nickname {

  public static final int MAX_NICKNAME_LENGTH = 40;
  public static final String NICKNAME_REGEX = "^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]{1,16}";
  public static final String NICKNAME_INVALID = "닉네임은 1~16자 한글, 영어, 숫자만 가능합니다.";

  private static final Pattern NICKNAME_FORMAT = Pattern.compile(NICKNAME_REGEX);

  private String nickName;

  public static Nickname from(String nickname) {
    if (isInvalidFormat(nickname)) {
      throw new IllegalArgumentException(NICKNAME_INVALID);
    }
    return new Nickname(nickname);
  }

  private static boolean isInvalidFormat(String nickname) {
    return !NICKNAME_FORMAT.matcher(nickname).find();
  }

  public String get() {
    return this.nickName;
  }
}
