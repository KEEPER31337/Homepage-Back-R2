package com.keeper.homepage.domain.about.dto.response;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.about.entity.StaticWriteSubtitleImage;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class StaticWriteSubTitleImageResponse {

  private Long id;

  private String subtitle;

  private String thumbnailPath;

  private Integer displayOrder;

  private List<StaticWriteContentResponse> staticWriteContents;

  public static StaticWriteSubTitleImageResponse from(StaticWriteSubtitleImage subtitleImage) {
    return new StaticWriteSubTitleImageResponse(subtitleImage.getId(),
        subtitleImage.getSubtitle(),
        subtitleImage.getThumbnail() != null ? subtitleImage.getThumbnail().getPath() : null,
        subtitleImage.getDisplayOrder(),
        subtitleImage.getStaticWriteContents().stream()
            .map(StaticWriteContentResponse::from)
            .collect(toList()));
  }
}
