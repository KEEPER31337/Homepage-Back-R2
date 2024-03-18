package com.keeper.homepage.domain.seminar.dto.request;

import com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.lang.Nullable;

@Builder
public record SeminarAttendanceStatusRequest(
    @Nullable
    String excuse,

    @NotNull(message = "출석 상태를 선택해주세요.")
    SeminarAttendanceStatusType statusType
) {

}
