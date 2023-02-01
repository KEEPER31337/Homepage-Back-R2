package com.keeper.homepage.domain.about.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.about.entity.StaticWriteContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StaticWriteContentResponse {

  private final Long id;

  private final String content;

  private final Integer displayOrder;

  public static StaticWriteContentResponse from(StaticWriteContent staticWriteContent) {
    return StaticWriteContentResponse.builder()
        .id(staticWriteContent.getId())
        .content(staticWriteContent.getContent())
        .displayOrder(staticWriteContent.getDisplayOrder())
        .build();
  }

  @Builder
  private StaticWriteContentResponse(Long id, String content, Integer displayOrder) {
    this.id = id;
    this.content = content;
    this.displayOrder = displayOrder;
  }
}
