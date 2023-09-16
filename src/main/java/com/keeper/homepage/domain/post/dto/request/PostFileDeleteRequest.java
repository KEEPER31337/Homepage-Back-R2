package com.keeper.homepage.domain.post.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record PostFileDeleteRequest(
    @NotNull(message = "File ID 리스트를 입력해주세요.")
    List<@NotNull Long> fileIds
) {

}
