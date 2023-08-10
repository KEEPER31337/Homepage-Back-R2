package com.keeper.homepage.domain.merit.dto.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateMeritTypeRequest {

  @NotNull(message = "변경할 점수를 입력해주세요.")
  private Integer score;

  @NotEmpty(message = "변경할 사유에 대해서 입력해주세요.")
  private String reason;

}
