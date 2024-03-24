package com.keeper.homepage.domain.calendar.application

import com.keeper.homepage.IntegrationTest
import com.keeper.homepage.domain.calendar.entity.Calendar
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CalendarServiceTest: IntegrationTest() {

    @Nested
    inner class `캘린더 저장` {

        lateinit var calendar: Calendar

        @BeforeEach
        fun setUp() {
            calendar = calendarTestHelper.generate()
        }

        @Test
        fun `캘린더 저장`() {

        }
    }

}