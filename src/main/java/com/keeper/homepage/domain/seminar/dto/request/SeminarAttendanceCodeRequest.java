package com.keeper.homepage.domain.seminar.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SeminarAttendanceCodeRequest(
    @NotNull(message = "출석 코드를 입력해주세요.")
    String attendanceCode
) {

}
