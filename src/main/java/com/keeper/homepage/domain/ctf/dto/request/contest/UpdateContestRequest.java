package com.keeper.homepage.domain.ctf.dto.request.contest;

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
public class UpdateContestRequest {

  public static final int CONTEST_NAME_LENGTH = 20;
  public static final int CONTEST_DESCRIPTION_LENGTH = 50;

  @NotBlank(message = "CTF 대회명을 입력해주세요.")
  @Size(max = CONTEST_NAME_LENGTH, message = "CTF 대회명은 {max}자 이하로 입력해주세요.")
  private String name;

  @NotBlank(message = "CTF 대회 설명을 입력해주세요.")
  @Size(max = CONTEST_DESCRIPTION_LENGTH, message = "CTF 대회 설명은 {max}자 이하로 입력해주세요.")
  private String description;

  @NotNull(message = "공개 비공개 여부를 입력해주세요.")
  private boolean isJoinable;

}
