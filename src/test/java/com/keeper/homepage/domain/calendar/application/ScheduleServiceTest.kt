package com.keeper.homepage.domain.schedule.application

import com.keeper.homepage.IntegrationTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class scheduleServiceTest: IntegrationTest() {

    companion object {
        const val DEFAULT_SCHEDULE_NAME = "Default Name"
        const val DEFAULT_SCHEDULE_TYPE = "일반"
        const val DEFAULT_SCHEDULE_TYPE_DESCRIPTION = "정기 세미나, 기술문서 발표, 1학기 시작/종료 등"
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class `캘린더 저장` {

        @Test
        fun `캘린더 저장을 성공해야 한다`() {
            val startTime = LocalDateTime.now()
            val endTime = startTime.plusDays(1)

            val savedScheduleId = scheduleService.saveSchedule(DEFAULT_SCHEDULE_NAME, startTime, endTime, 1L)
            val findSchedule = scheduleRepository.findByIdOrNull(savedScheduleId) ?: fail("캘린더 저장 실패")

            assertEquals(findSchedule.name, DEFAULT_SCHEDULE_NAME)
            assertEquals(findSchedule.startTime, startTime)
            assertEquals(findSchedule.endTime, endTime)
            assertEquals(findSchedule.scheduleType.type, DEFAULT_SCHEDULE_TYPE)
            assertEquals(findSchedule.scheduleType.description, DEFAULT_SCHEDULE_TYPE_DESCRIPTION)
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class `캘린더 조회` {

        @Test
        fun `캘린더 조회를 성공해야 한다`() {
            scheduleService.saveSchedule( // 2021-05-10 ~ 2021-07-10
                DEFAULT_SCHEDULE_NAME,
                LocalDateTime.of(2021, 5, 10, 0, 0),
                LocalDateTime.of(2021, 7, 10, 0, 0),
                1L
            )

            scheduleService.saveSchedule( // 2021-05-10 ~ 2021-06-10
                DEFAULT_SCHEDULE_NAME,
                LocalDateTime.of(2021, 5, 10, 0, 0),
                LocalDateTime.of(2021, 6, 10, 0, 0),
                1L
            )

            scheduleService.saveSchedule( // 2021-06-10 ~ 2021-07-10
                DEFAULT_SCHEDULE_NAME,
                LocalDateTime.of(2021, 6, 10, 0, 0),
                LocalDateTime.of(2021, 7, 10, 0, 0),
                1L
            )

            scheduleService.saveSchedule( // 2021-06-10 ~ 2021-06-20
                DEFAULT_SCHEDULE_NAME,
                LocalDateTime.of(2021, 6, 10, 0, 0),
                LocalDateTime.of(2021, 6, 20, 0, 0),
                1L
            )

            em.flush()
            em.clear()

            val findSchedule = scheduleService.findAllSchedule(
                LocalDateTime.of(2021, 6, 11, 0, 0),
                LocalDateTime.of(2021, 6, 19, 0, 0)
            )

            val findOtherSchedule = scheduleService.findAllSchedule(
                LocalDateTime.of(2021, 6, 1, 0, 0),
                LocalDateTime.of(2021, 6, 30, 0, 0)
            )

            assertEquals(findSchedule.size, 3)
            assertEquals(findOtherSchedule.size, 4)
        }
    }
}
