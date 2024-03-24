package com.keeper.homepage.domain.calendar.application

import com.keeper.homepage.domain.calendar.dao.CalendarRepository
import com.keeper.homepage.domain.calendar.dao.ScheduleTypeRepository
import com.keeper.homepage.domain.calendar.entity.Calendar
import com.keeper.homepage.domain.calendar.entity.ScheduleType
import lombok.RequiredArgsConstructor
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class CalendarService(
    val calendarRepository: CalendarRepository,
    val scheduleTypeRepository: ScheduleTypeRepository,
) {

    @Transactional
    fun saveCalendar(name: String, startDateTime: LocalDateTime,
        endDateTime: LocalDateTime, scheduleTypeId: Long,
    ): Long {
        val findScheduleId = scheduleTypeRepository.findByIdOrNull(scheduleTypeId)
            ?: throw IllegalArgumentException("ScheduleType not found")

        return calendarRepository.save(
            Calendar(
                name = name,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                scheduleType = findScheduleId,
            )
        ).id!!
    }
}