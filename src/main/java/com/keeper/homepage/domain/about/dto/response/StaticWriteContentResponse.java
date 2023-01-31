package com.keeper.homepage.domain.about.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.about.entity.StaticWriteContent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class StaticWriteContentResponse {

  private Long id;

  private String content;

  private Integer displayOrder;

  public static StaticWriteContentResponse from(StaticWriteContent staticWriteContent) {
    return new StaticWriteContentResponse(staticWriteContent.getId(),
        staticWriteContent.getContent(), staticWriteContent.getDisplayOrder());
  }
}
