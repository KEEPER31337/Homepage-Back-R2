package com.keeper.homepage.domain.calendar.dto.response

import com.keeper.homepage.domain.Schedule.entity.Schedule
import com.keeper.homepage.domain.calendar.entity.ScheduleType
import java.time.LocalDateTime

data class FindAllScheduleResponse(
    val id: Long,
    val name: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val scheduleTypeResponse: ScheduleTypeResponse,
) {
    companion object {
        fun from(schedule: Schedule): FindAllScheduleResponse {
            return FindAllScheduleResponse(
                id = schedule.id!!,
                name = schedule.name,
                startTime = schedule.startTime,
                endTime = schedule.endTime,
                scheduleTypeResponse = ScheduleTypeResponse.from(schedule.scheduleType),
            )
        }
    }
}
