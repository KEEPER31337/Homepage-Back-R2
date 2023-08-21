package com.keeper.homepage.domain.election.dto.request;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class ElectionCandidateRegisterRequest {

  @NotNull(message = "설명을 입력해주세요.")
  private String description;
  @NotNull(message = "선거 직위 ID는 필수 입력입니다.")
  private Long memberJobId;

}
