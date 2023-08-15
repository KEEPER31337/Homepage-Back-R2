package com.keeper.homepage.domain.merit.dto.request;

import com.keeper.homepage.domain.merit.entity.MeritType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class AddMeritTypeRequest {

  @NotNull(message = "상벌점 점수를 입력해주세요.")
  private Integer score;

  @NotEmpty(message = "상벌점 타입에 대해서 입력해주세요.")
  private String reason;

  public AddMeritTypeRequest from(MeritType meritType) {
    return AddMeritTypeRequest.builder()
        .score(meritType.getMerit())
        .reason(meritType.getDetail())
        .build();
  }

}
