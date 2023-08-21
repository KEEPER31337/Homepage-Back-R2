package com.keeper.homepage.domain.election.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ElectionRegisterRequest {

  @NotBlank(message = "설명란을 입력해주세요.")
  private String description;

  @NotBlank(message = "회원 역할을 입력해주세요.")
  @Pattern(regexp = "^(1|2|8)$", message = "회원 역할은 회장, 부회장, 총무만 가능합니다.")
  private String memberJobId;

}
