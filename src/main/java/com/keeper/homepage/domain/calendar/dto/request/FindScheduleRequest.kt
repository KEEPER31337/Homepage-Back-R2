package com.keeper.homepage.domain.calendar.dto.request

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class FindScheduleRequest(
    @NotNull(message = "시작 시간을 입력해주세요.")
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val startTime: LocalDateTime,

    @NotNull(message = "종료 시간을 입력해주세요.")
    @field:JsonFormat(pattern = "yyyy.MM.dd'T'HH:mm:ss")
    val endTime: LocalDateTime,
)
