package com.keeper.homepage.domain.calendar.dto.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.keeper.homepage.global.error.BusinessException
import com.keeper.homepage.global.error.ErrorCode
import com.keeper.homepage.global.util.time.diff
import com.keeper.homepage.global.util.time.isNotBefore
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.time.Period

data class FindScheduleRequest(
    @NotNull(message = "시작 시간을 입력해주세요.")
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val startTime: LocalDateTime,

    @NotNull(message = "종료 시간을 입력해주세요.")
    @field:JsonFormat(pattern = "yyyy.MM.dd'T'HH:mm:ss")
    val endTime: LocalDateTime,
) {
    init {
        if (startTime isNotBefore endTime) throw BusinessException(endTime, "endTime", ErrorCode.END_TIME_IS_EARLIER_THAN_START_TIME)
        if (startTime diff endTime > 31) throw BusinessException(endTime, "endTime", ErrorCode.END_TIME_IS_TOO_LONG)
    }
}
