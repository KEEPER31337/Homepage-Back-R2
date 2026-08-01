package com.keeper.homepage.domain.vote.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record VoteSelectionRequest(
    @JsonProperty("agenda_id")
    @NotNull(message = "안건 ID를 입력해주세요.")
    @Positive(message = "안건 ID는 양수여야 합니다.")
    Long agendaId,

    @JsonProperty("option_ids")
    @NotEmpty(message = "선택지 ID를 한 개 이상 입력해주세요.")
    List<@NotNull(message = "선택지 ID는 null일 수 없습니다.")
        @Positive(message = "선택지 ID는 양수여야 합니다.") Long> optionIds
) {

}
