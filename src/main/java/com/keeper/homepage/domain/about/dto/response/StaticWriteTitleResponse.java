package com.keeper.homepage.domain.about.dto.response;

import static java.util.stream.Collectors.toList;

import com.keeper.homepage.domain.about.entity.StaticWriteTitle;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StaticWriteTitleResponse {

  private final Long id;
  private final String title;
  private final String type;
  private final List<StaticWriteSubTitleImageResponse> subtitleImages;

  public static StaticWriteTitleResponse from(StaticWriteTitle title) {
    return StaticWriteTitleResponse.builder()
        .id(title.getId())
        .title(title.getTitle())
        .type(title.getStaticWriteTitleType().getType())
        .subtitleImages(title.getStaticWriteSubtitleImages().stream()
            .map(StaticWriteSubTitleImageResponse::from)
            .collect(toList()))
        .build();
  }

  @Builder
  private StaticWriteTitleResponse(Long id, String title, String type,
      List<StaticWriteSubTitleImageResponse> subtitleImages) {
    this.id = id;
    this.title = title;
    this.type = type;
    this.subtitleImages = subtitleImages;
  }
}
