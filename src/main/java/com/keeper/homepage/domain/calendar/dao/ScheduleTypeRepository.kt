package com.keeper.homepage.domain.calendar.dao

import com.keeper.homepage.domain.calendar.entity.ScheduleType
import org.springframework.data.jpa.repository.JpaRepository

interface ScheduleTypeRepository: JpaRepository<ScheduleType, Long> {
}