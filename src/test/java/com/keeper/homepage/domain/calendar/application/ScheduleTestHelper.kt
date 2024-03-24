package com.keeper.homepage.domain.Schedule.application

import com.keeper.homepage.domain.Schedule.entity.Schedule
import com.keeper.homepage.domain.calendar.application.ScheduleTypeTestHelper
import com.keeper.homepage.domain.calendar.dao.ScheduleRepository
import com.keeper.homepage.domain.calendar.entity.ScheduleType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ScheduleTestHelper {

    @Autowired
    lateinit var scheduleRepository: ScheduleRepository

    @Autowired
    lateinit var scheduleTypeTestHelper: ScheduleTypeTestHelper

    fun generate(): Schedule {
        return ScheduleBuilder().build()
    }

    fun builder(): ScheduleBuilder {
        return ScheduleBuilder()
    }

    inner class ScheduleBuilder {

        private var name: String? = null
        private var startTime: LocalDateTime? = null
        private var endTime: LocalDateTime? = null
        private var scheduleType: ScheduleType? = null

        private fun ScheduleBuilder() {}

        fun name(name: String): ScheduleBuilder {
            this.name = name
            return this
        }

        fun startDateTime(startTime: LocalDateTime): ScheduleBuilder {
            this.startTime = startTime
            return this
        }

        fun endTime(endTime: LocalDateTime): ScheduleBuilder {
            this.endTime = endTime
            return this
        }

        fun scheduleType(scheduleType: ScheduleType): ScheduleBuilder {
            this.scheduleType = scheduleType
            return this
        }

        fun build(): Schedule {
            return scheduleRepository.save(
                Schedule(
                    name = name ?: "Default Name",
                    startTime = startTime ?: LocalDateTime.now(),
                    endTime = endTime ?: LocalDateTime.now(),
                    scheduleType = scheduleType ?: scheduleTypeTestHelper.generate(),
                )
            )
        }
    }
}