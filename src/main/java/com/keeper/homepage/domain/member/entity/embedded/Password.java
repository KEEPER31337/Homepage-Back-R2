package com.keeper.homepage.domain.member.entity.embedded;

import com.keeper.homepage.global.config.password.PasswordFactory;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"password"})
public class Password {

  public static final int HASHED_PASSWORD_MAX_LENGTH = 512;
  public static final String PASSWORD_INVALID = "비밀번호는 8~20자여야 하고 영어, 숫자가 포함되어야 합니다.";
  public static final String PASSWORD_REGEX = "^(?=.*?[A-Za-z])(?=.*?\\d).{8,20}$";

  private static final Pattern PASSWORD_FORMAT = Pattern.compile(PASSWORD_REGEX);

  private String password;

  public static Password from(String rawPassword) {
    return new Password(PasswordFactory.getPasswordEncoder().encode(rawPassword));
  }

  public static Password from(String rawPassword, PasswordEncoder passwordEncoder) {
    if (isInvalidFormat(rawPassword)) {
      throw new IllegalArgumentException(PASSWORD_INVALID);
    }
    return new Password(passwordEncoder.encode(rawPassword));
  }

  public String get() {
    return this.password;
  }

  private static boolean isInvalidFormat(String rawPassword) {
    return !PASSWORD_FORMAT.matcher(rawPassword).find();
  }

  public boolean isWrongPassword(String rawPassword) {
    return !PasswordFactory.getPasswordEncoder().matches(rawPassword, this.password);
  }
}
