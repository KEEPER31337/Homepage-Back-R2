package com.keeper.homepage.domain.calendar.dao

import com.keeper.homepage.domain.Schedule.entity.Schedule
import org.springframework.data.jpa.repository.JpaRepository

interface ScheduleRepository: JpaRepository<Schedule, Long> {
}
