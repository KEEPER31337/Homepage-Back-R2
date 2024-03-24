package com.keeper.homepage.domain.schedule.application

import com.keeper.homepage.IntegrationTest
import com.keeper.homepage.domain.Schedule.entity.Schedule
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull

class scheduleServiceTest: IntegrationTest() {

    @Nested
    inner class `캘린더 저장` {

        lateinit var schedule: Schedule

        @BeforeEach
        fun setUp() {
            schedule = scheduleTestHelper.generate()
        }

        @Test
        fun `캘린더 저장을 성공해야 한다`() {
            em.flush()
            em.clear()



            val findSchedule = scheduleRepository.findByIdOrNull(schedule.id) ?: fail("캘린더 저장 실패")

            assertEquals(schedule.name, findSchedule.name)
            assertEquals(schedule.scheduleType.type, findSchedule.scheduleType.type)
            assertEquals(schedule.scheduleType.description, findSchedule.scheduleType.description)
            assertEquals(schedule.scheduleType.id, findSchedule.scheduleType.id)
        }
    }

}