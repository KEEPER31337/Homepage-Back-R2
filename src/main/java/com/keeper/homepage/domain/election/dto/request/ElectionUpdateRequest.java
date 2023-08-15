package com.keeper.homepage.domain.election.dto.request;

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
public class ElectionUpdateRequest {

  public static final int ELECTION_NAME_LENGTH = 20;
  public static final int ELECTION_DESCRIPTION_LENGTH = 50;

  @NotBlank(message = "선거 생성 정보를 입력해주세요.")
  @Size(max = ELECTION_NAME_LENGTH, message = "선거 이름은 {max}자 이내로 작성해주세요.")
  private String name;

  @Size(max = ELECTION_DESCRIPTION_LENGTH, message = "선거 설명은 {max}자 이내로 작성해주세요.")
  private String description;

  @NotNull
  private Boolean isAvailable;

}
