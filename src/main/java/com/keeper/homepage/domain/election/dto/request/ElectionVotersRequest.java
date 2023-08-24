package com.keeper.homepage.domain.election.dto.request;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class ElectionVotersRequest {

  @NotEmpty(message = "투표자의 아이디를 입력해주세요.")
  private List<@NotNull Long> voterIds;

}
