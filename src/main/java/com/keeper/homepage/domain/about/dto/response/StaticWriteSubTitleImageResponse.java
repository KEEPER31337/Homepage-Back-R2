package com.keeper.homepage.domain.about.dto.response;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.about.entity.StaticWriteSubtitleImage;
import com.keeper.homepage.domain.about.entity.StaticWriteTitle;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StaticWriteSubTitleImageResponse {

  private final Long id;
  private final String subtitle;
  private final String thumbnailPath;
  private final Integer displayOrder;
  private final List<StaticWriteContentResponse> staticWriteContents;

  public static StaticWriteSubTitleImageResponse from(StaticWriteSubtitleImage subtitleImage) {
    return StaticWriteSubTitleImageResponse.builder()
        .id(subtitleImage.getId())
        .subtitle(subtitleImage.getSubtitle())
        .thumbnailPath(subtitleImage.getThumbnail() != null ? subtitleImage.getThumbnail().getPath() : null)
        .displayOrder(subtitleImage.getDisplayOrder())
        .staticWriteContents(subtitleImage.getStaticWriteContents().stream()
            .map(StaticWriteContentResponse::from)
            .collect(toList()))
        .build();
  }

  @Builder
  private StaticWriteSubTitleImageResponse(Long id, String subtitle, String thumbnailPath,
      Integer displayOrder, List<StaticWriteContentResponse> staticWriteContents) {
    this.id = id;
    this.subtitle = subtitle;
    this.thumbnailPath = thumbnailPath;
    this.displayOrder = displayOrder;
    this.staticWriteContents = staticWriteContents;
  }
}
