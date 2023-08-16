package com.keeper.homepage.domain.point.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class presentPointRequest {

  @NotNull(message = "포인트를 입력해주세요.")
  @PositiveOrZero
  private int point;

  @NotNull(message = "보낼 회원의 ID를 입력해주세요.")
  private long memberId;

  private String message;
}
