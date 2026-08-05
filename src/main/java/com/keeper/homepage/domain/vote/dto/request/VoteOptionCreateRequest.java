package com.keeper.homepage.domain.vote.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VoteOptionCreateRequest(
    @NotBlank(message = "선택지 내용을 입력해주세요.")
    @Size(max = 500, message = "선택지 내용은 {max}자 이하로 입력해주세요.")
    String content
) {

}
