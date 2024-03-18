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
@Builder
@AllArgsConstructor(access = PRIVATE)
public class StaticWriteSubTitleImageResponse {

  private Long id;
  private String subtitle;
  private String thumbnailPath;
  private Integer displayOrder;
  private List<StaticWriteContentResponse> staticWriteContents;

  public static StaticWriteSubTitleImageResponse from(StaticWriteSubtitleImage subtitleImage) {
    return StaticWriteSubTitleImageResponse.builder()
        .id(subtitleImage.getId())
        .subtitle(subtitleImage.getSubtitle())
        .thumbnailPath(
            subtitleImage.getThumbnail() != null ? subtitleImage.getThumbnail().getPath() : null)
        .displayOrder(subtitleImage.getDisplayOrder())
        .staticWriteContents(subtitleImage.getStaticWriteContents().stream()
            .map(StaticWriteContentResponse::from)
            .collect(toList()))
        .build();
  }
}
