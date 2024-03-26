package com.keeper.homepage.domain.calendar.dto.request

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import java.time.LocalDateTime

data class SaveScheduleRequest(

    @field:NotEmpty(message = "일정 이름을 입력해주세요.")
    val name: String,

    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val startTime: LocalDateTime,

    @field:JsonFormat(pattern = "yyyy.MM.dd'T'HH:mm:ss")
    val endTime: LocalDateTime,

    @field:Pattern(message = "일정 타입을 입력해주세요.", regexp = "^[1234]+\$")
    val scheduleTypeId: String,
)
