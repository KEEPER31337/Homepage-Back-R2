package com.keeper.homepage.domain.calendar.dao

import com.keeper.homepage.domain.calendar.entity.Calendar
import org.springframework.data.jpa.repository.JpaRepository

interface CalendarRepository: JpaRepository<Calendar, Long> {
}
