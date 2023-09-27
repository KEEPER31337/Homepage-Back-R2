package com.keeper.homepage.domain.seminar.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.lang.Nullable;

@Builder
public record SeminarStartRequest(
    @Nullable
    @Future(message = "현재 시간 이전 시간으로 설정할 수 없습니다.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime attendanceCloseTime,

    @Nullable
    @Future(message = "현재 시간 이전 시간으로 설정할 수 없습니다.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime latenessCloseTime) {

}
