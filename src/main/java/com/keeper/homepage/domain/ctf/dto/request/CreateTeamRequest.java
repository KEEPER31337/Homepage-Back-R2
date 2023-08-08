package com.keeper.homepage.domain.ctf.dto.request;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class CreateTeamRequest {

  public static final int TEAM_NAME_LENGTH = 20;
  public static final int TEAM_DESCRIPTION_LENGTH = 40;

  @NotBlank(message = "CTF 팀명을 입력해주세요.")
  @Size(max = TEAM_NAME_LENGTH, message = "CTF 팀명은 {max}자 이하로 입력해주세요.")
  private String name;

  @NotBlank(message = "CTF 팀 설명을 입력해주세요.")
  @Size(max = TEAM_DESCRIPTION_LENGTH, message = "CTF 팀 설명은 {max}자 이하로 입력해주세요.")
  private String description;

  @NotNull(message = "CTF Contest ID를 입력해주세요.")
  private long contestId;
}
