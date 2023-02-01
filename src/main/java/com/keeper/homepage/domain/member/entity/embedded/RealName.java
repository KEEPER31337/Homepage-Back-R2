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
@EqualsAndHashCode(of = {"realName"})
public class RealName {

  public static final int MAX_REAL_NAME_LENGTH = 40;
  public static final String REAL_NAME_REGEX = "^[a-zA-Z가-힣]{1,20}";
  public static final String REAL_NAME_INVALID = "실명은 1~20자 한글, 영어만 가능합니다.";

  private static final Pattern REAL_NAME_FORMAT = Pattern.compile(REAL_NAME_REGEX);

  private String realName;

  public static RealName from(String realName) {
    if (isInvalidFormat(realName)) {
      throw new IllegalArgumentException(REAL_NAME_INVALID);
    }
    return new RealName(realName);
  }

  private static boolean isInvalidFormat(String realName) {
    return !REAL_NAME_FORMAT.matcher(realName).find();
  }

  public String get() {
    return this.realName;
  }
}
