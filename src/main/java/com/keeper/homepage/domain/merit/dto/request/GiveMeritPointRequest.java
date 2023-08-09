package com.keeper.homepage.domain.merit.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class GiveMeritPointRequest {

  @NotNull(message = "수여자를 입력해주세요.")
  private long awarderId;

  @NotNull(message = "수상자를 입력해주세요.")
  private long giverId;

  @NotNull(message = "상벌점 이유에 대해서 입력해주세요.")
  private String reason;

}
