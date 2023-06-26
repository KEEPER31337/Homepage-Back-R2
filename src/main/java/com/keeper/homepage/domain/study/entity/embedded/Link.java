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
  private NoteLink noteLink;

  private String etcLink;

  @Builder
  private Link(GitLink gitLink, NoteLink noteLink, String etcLink) {
    this.gitLink = gitLink;
    this.noteLink = noteLink;
    this.etcLink = etcLink;
  }
}
