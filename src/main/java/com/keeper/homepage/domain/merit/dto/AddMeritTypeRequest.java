package com.keeper.homepage.domain.merit.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class AddMeritTypeRequest {

  @NotNull(message = "상점을 입력해주세요.")
  private int reward;

  @NotNull(message = "벌점을 입력해주세요.")
  private int penalty;

  @NotNull(message = "상벌점 타입에 대해서 입력해주세요.")
  private String detail;

}
