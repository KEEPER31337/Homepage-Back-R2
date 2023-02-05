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
@EqualsAndHashCode(of = {"emailAddress"})
public class EmailAddress {

  public static final String EMAIL_INVALID = "이메일 형식이 유효하지 않습니다.";
  public static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}";
  public static final int MAX_EMAIL_LENGTH = 250;

  private static final Pattern EMAIL_FORMAT = Pattern.compile(EMAIL_REGEX);

  private String emailAddress;

  public static EmailAddress from(String emailAddress) {
    if (isInvalidFormat(emailAddress)) {
      throw new IllegalArgumentException(EMAIL_INVALID);
    }
    return new EmailAddress(emailAddress);
  }

  private static boolean isInvalidFormat(String emailAddress) {
    return !EMAIL_FORMAT.matcher(emailAddress).find();
  }

  public String get() {
    return this.emailAddress;
  }
}
