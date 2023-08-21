package com.keeper.homepage.domain.study.entity.embedded;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class GitLink {

  public static final String GIT_LINK_INVALID = "https://github.com 로 시작하는 깃허브 주소를 입력해주세요.";
  public static final String GIT_LINK_REGEX = "^https://github.com/\\S+$";

  private static final Pattern GIT_LINK_FORMAT = Pattern.compile(GIT_LINK_REGEX);

  private String gitLink;

  public static GitLink from(String gitLink) {
    if (isInvalidFormat(gitLink)) {
      throw new IllegalArgumentException(GIT_LINK_INVALID);
    }
    return new GitLink(gitLink);
  }

  private static boolean isInvalidFormat(String gitLink) {
    return !GIT_LINK_FORMAT.matcher(gitLink).find();
  }

  public String get() {
    return this.gitLink;
  }
}
