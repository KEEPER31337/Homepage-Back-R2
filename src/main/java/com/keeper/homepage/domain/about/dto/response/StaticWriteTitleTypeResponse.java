package com.keeper.homepage.domain.about.dto.response;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.about.entity.StaticWriteTitle;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class StaticWriteTitleTypeResponse {

  private List<String> list;

  public static StaticWriteTitleTypeResponse from(List<StaticWriteTitle> staticWriteTitles) {
    return new StaticWriteTitleTypeResponse(staticWriteTitles.stream()
        .map(staticWriteTitle -> staticWriteTitle.getStaticWriteTitleType().getType())
        .collect(toList()));
  }
}
