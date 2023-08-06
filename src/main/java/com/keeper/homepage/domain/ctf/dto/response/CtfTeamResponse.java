package com.keeper.homepage.domain.ctf.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.ctf.entity.team.CtfTeam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class CtfTeamResponse {

  private Long id;
  private String name;

  public static CtfTeamResponse from(CtfTeam ctfTeam) {
    return CtfTeamResponse.builder()
        .id(ctfTeam.getId())
        .name(ctfTeam.getName())
        .build();
  }
}
