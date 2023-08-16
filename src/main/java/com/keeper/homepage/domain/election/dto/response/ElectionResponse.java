package com.keeper.homepage.domain.election.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.election.entity.Election;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class ElectionResponse {

  private Long id;
  private String name;
  private String description;
  private Boolean isAvailable;

  public static ElectionResponse from(Election election) {
    return ElectionResponse.builder()
        .id(election.getId())
        .name(election.getName())
        .description(election.getDescription())
        .isAvailable(election.getIsAvailable())
        .build();
  }

}
