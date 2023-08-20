package com.keeper.homepage.domain.study.dto.response;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class LinkResponse {

  private String title;
  private String content;

  public static LinkResponse of(String title, String content) {
    return LinkResponse.builder()
        .title(title)
        .content(content)
        .build();
  }
}
