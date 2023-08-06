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
public class NotionLink {

  public static final String NOTION_LINK_INVALID = "https://www.notion.so/ 로 시작하는 노션 주소를 입력해주세요.";
  public static final String NOTION_LINK_REGEX = "^https://www.notion.so/\\S+$";

  private static final Pattern NOTION_LINK_FORMAT = Pattern.compile(NOTION_LINK_REGEX);

  private String notionLink;

  public static NotionLink from(String notionLink) {
    if (isInvalidFormat(notionLink)) {
      throw new IllegalArgumentException(NOTION_LINK_INVALID);
    }
    return new NotionLink(notionLink);
  }

  private static boolean isInvalidFormat(String noteLink) {
    return !NOTION_LINK_FORMAT.matcher(noteLink).find();
  }

  public String get() {
    return this.notionLink;
  }
}
