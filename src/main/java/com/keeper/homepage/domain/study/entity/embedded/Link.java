package com.keeper.homepage.domain.study.entity.embedded;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class Link {

  @Embedded
  private GitLink gitLink;

  @Embedded
  private NotionLink notionLink;

  private String etcLink;

  @Builder
  private Link(GitLink gitLink, NotionLink notionLink, String etcLink) {
    this.gitLink = gitLink;
    this.notionLink = notionLink;
    this.etcLink = etcLink;
  }

  public boolean isEmpty() {
    return gitLink == null && notionLink == null && etcLink == null;
  }
}
