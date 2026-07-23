package com.keeper.homepage.domain.calendar.dto.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.keeper.homepage.domain.calendar.entity.ScheduleType
import com.keeper.homepage.global.error.BusinessException
import com.keeper.homepage.global.error.ErrorCode
import com.keeper.homepage.global.util.time.diff
import com.keeper.homepage.global.util.time.isNotBefore
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import java.time.LocalDateTime

data class SaveScheduleRequest(

    @field:NotEmpty(message = "일정 이름을 입력해주세요.")
    val name: String,

    @field:NotNull(message = "시작 시간을 입력해주세요.")
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val startTime: LocalDateTime,

    @field:NotNull(message = "종료 시간을 입력해주세요.")
    @field:JsonFormat(pattern = "yyyy.MM.dd'T'HH:mm:ss")
    val endTime: LocalDateTime,

    @field:NotNull(message = "일정 타입을 입력해주세요.")
    val scheduleType: ScheduleType.Type,
) {
    init {
        if (startTime isNotBefore endTime) throw BusinessException(endTime, "endTime", ErrorCode.END_TIME_IS_EARLIER_THAN_START_TIME)
        if (startTime diff endTime > 31) throw BusinessException(endTime, "endTime", ErrorCode.END_TIME_IS_TOO_LONG)
    }
}
