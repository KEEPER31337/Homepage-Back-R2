package com.keeper.homepage.domain.study.entity.embedded;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class NotionLink {

  private String notionLink;

  public static NotionLink from(String notionLink) {
    return new NotionLink(notionLink);
  }

  public String get() {
    return this.notionLink;
  }
}
