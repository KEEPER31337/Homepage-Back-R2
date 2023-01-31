package com.keeper.homepage.domain.about.dto.response;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.about.entity.StaticWriteTitle;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class StaticWriteTitleResponse {

  private Long id;

  private String title;

  private String type;

  private List<StaticWriteSubTitleImageResponse> subtitleImages;

  public static StaticWriteTitleResponse from(StaticWriteTitle title) {
    return new StaticWriteTitleResponse(title.getId(),
        title.getTitle(),
        title.getType().getType(),
        title.getStaticWriteSubtitleImages().stream()
            .map(StaticWriteSubTitleImageResponse::from)
            .collect(toList()));
  }
}
