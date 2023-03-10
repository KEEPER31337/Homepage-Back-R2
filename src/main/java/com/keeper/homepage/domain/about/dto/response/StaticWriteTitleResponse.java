package com.keeper.homepage.domain.about.dto.response;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.about.entity.StaticWriteTitle;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class StaticWriteTitleResponse {

  private Long id;
  private String title;
  private String type;
  private List<StaticWriteSubTitleImageResponse> subtitleImages;

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
}
