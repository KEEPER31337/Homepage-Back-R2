package com.keeper.homepage.domain.vote.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record VoteParticipationRequest(
    @NotEmpty(message = "안건별 선택 정보를 한 개 이상 입력해주세요.")
    List<@NotNull(message = "선택 정보는 null일 수 없습니다.")
        @Valid VoteSelectionRequest> selections
) {
}
