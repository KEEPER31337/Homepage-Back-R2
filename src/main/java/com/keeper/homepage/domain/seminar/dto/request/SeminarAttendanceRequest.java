package com.keeper.homepage.domain.seminar.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SeminarAttendanceRequest(
    @NotNull(message = "세미나 ID를 입력해주세요.")
    Long id,

    @NotNull(message = "출석 코드를 입력해주세요.")
    String attendanceCode
) {

}
