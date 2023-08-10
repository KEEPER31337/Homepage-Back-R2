package com.keeper.homepage.domain.merit.dto.request;


import jakarta.validation.constraints.NotEmpty;
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

  @NotEmpty(message = "상벌점 사유를 선택해주세요.")
  private long meritTypeId;

}
