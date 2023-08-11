package com.keeper.homepage.domain.ctf.dto.request.team;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class JoinTeamRequest {

  @NotNull(message = "CTF Contest ID를 입력해주세요.")
  private long contestId;

}
