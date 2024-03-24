package com.keeper.homepage.domain.calendar.application

import com.keeper.homepage.domain.calendar.dao.CalendarRepository
import com.keeper.homepage.domain.calendar.dao.ScheduleTypeRepository
import com.keeper.homepage.domain.calendar.entity.Calendar
import com.keeper.homepage.domain.calendar.entity.ScheduleType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.properties.Delegates

@Component
class CalendarTestHelper {

    @Autowired
    lateinit var calendarRepository: CalendarRepository

    @Autowired
    lateinit var scheduleTypeTestHelper: ScheduleTypeTestHelper

    fun generate(): Calendar {
        return CalendarBuilder().build()
    }

    fun builder(): CalendarBuilder {
        return CalendarBuilder()
    }

    inner class CalendarBuilder {

        private var name: String? = null
        private var startDateTime: LocalDateTime? = null
        private var endDateTime: LocalDateTime? = null
        private var scheduleType: ScheduleType? = null

        private fun CalendarBuilder() {}

        fun name(name: String): CalendarBuilder {
            this.name = name
            return this
        }

        fun startDateTime(startDateTime: LocalDateTime): CalendarBuilder {
            this.startDateTime = startDateTime
            return this
        }

        fun endDateTime(endDateTime: LocalDateTime): CalendarBuilder {
            this.endDateTime = endDateTime
            return this
        }

        fun scheduleType(scheduleType: ScheduleType): CalendarBuilder {
            this.scheduleType = scheduleType
            return this
        }

        fun build(): Calendar {
            return calendarRepository.save(
                Calendar(
                    name = name ?: "Default Name",
                    startDateTime = startDateTime ?: LocalDateTime.now(),
                    endDateTime = endDateTime ?: LocalDateTime.now(),
                    scheduleType = scheduleType ?: scheduleTypeTestHelper.generate(),
                )
            )
        }
    }
}