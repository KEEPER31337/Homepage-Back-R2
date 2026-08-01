package com.keeper.homepage.domain.vote.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record VoteAgendaCreateRequest(
    @NotBlank(message = "안건 제목을 입력해주세요.")
    @Size(max = 500, message = "안건 제목은 {max}자 이하로 입력해주세요.")
    String title,

    @NotNull(message = "최소 선택 수를 입력해주세요.")
    @Min(value = 1, message = "최소 선택 수는 {value} 이상이어야 합니다.")
    Integer minSelect,

    @NotNull(message = "최대 선택 수를 입력해주세요.")
    @Min(value = 1, message = "최대 선택 수는 {value} 이상이어야 합니다.")
    Integer maxSelect,

    @NotEmpty(message = "선택지를 한 개 이상 입력해주세요.")
    List<@NotNull(message = "선택지는 null일 수 없습니다.") @Valid VoteOptionCreateRequest> options
) {

  @JsonIgnore
  @AssertTrue(message = "최소 선택 수는 최대 선택 수 이하이고, 최대 선택 수는 선택지 수 이하여야 합니다.")
  public boolean isSelectionRangeValid() {
    if (minSelect == null || maxSelect == null || options == null || options.isEmpty()) {
      return true;
    }
    return minSelect <= maxSelect && maxSelect <= options.size();
  }
}
