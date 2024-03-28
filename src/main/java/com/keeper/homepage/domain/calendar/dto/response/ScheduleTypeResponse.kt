package com.keeper.homepage.domain.calendar.dto.response

import com.keeper.homepage.domain.calendar.entity.ScheduleType

data class ScheduleTypeResponse(
    val id: Long,
    val type: String,
    val description: String,
) {
    companion object {
        fun from(scheduleType: ScheduleType): ScheduleTypeResponse {
            return ScheduleTypeResponse(
                id = scheduleType.id!!,
                type = scheduleType.type,
                description = scheduleType.description,
            )
        }
    }
}
