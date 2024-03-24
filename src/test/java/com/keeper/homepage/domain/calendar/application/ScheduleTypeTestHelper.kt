package com.keeper.homepage.domain.calendar.application

import com.keeper.homepage.domain.calendar.dao.ScheduleTypeRepository
import com.keeper.homepage.domain.calendar.entity.ScheduleType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.properties.Delegates

@Component
class ScheduleTypeTestHelper {

    @Autowired
    lateinit var scheduleTypeRepository: ScheduleTypeRepository

    fun generate(): ScheduleType {
        return ScheduleTypeBuilder().build()
    }

    fun builder(): ScheduleTypeBuilder {
        return ScheduleTypeBuilder()
    }

    inner class ScheduleTypeBuilder {

        private var type: String? = null
        private var description: String? = null

        private fun ScheduleTypeBuilder() {}

        fun type(type: String): ScheduleTypeBuilder {
            this.type = type
            return this
        }

        fun description(description: String): ScheduleTypeBuilder {
            this.description = description
            return this
        }

        fun build(): ScheduleType = scheduleTypeRepository.save(
                ScheduleType(
                    type = type ?: "일반",
                    description = description ?: "정기 세미나, 기술문서 발표, 1학기 시작/종료 등",
                    id = null
                )
            )

    }
}