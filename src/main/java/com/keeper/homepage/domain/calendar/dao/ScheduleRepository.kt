package com.keeper.homepage.domain.calendar.dao

import com.keeper.homepage.domain.Schedule.entity.Schedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface ScheduleRepository: JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s WHERE s.startTime <= :endTime AND s.endTime >= :startTime")
    fun findAllSchedule(startTime: LocalDateTime, endTime: LocalDateTime): List<Schedule>

}
