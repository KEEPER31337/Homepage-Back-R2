package com.keeper.homepage.domain.calendar.application

import com.keeper.homepage.domain.Schedule.entity.Schedule
import com.keeper.homepage.domain.calendar.dao.ScheduleRepository
import com.keeper.homepage.domain.calendar.dao.ScheduleTypeRepository
import com.keeper.homepage.domain.calendar.dto.response.FindAllScheduleResponse
import com.keeper.homepage.global.error.BusinessException
import com.keeper.homepage.global.error.ErrorCode
import lombok.RequiredArgsConstructor
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class ScheduleService(
    val scheduleRepository: ScheduleRepository,
    val scheduleTypeRepository: ScheduleTypeRepository,
) {

    @Transactional
    fun saveSchedule(
        name: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        scheduleTypeId: Long,
    ): Long {
        val findSchedule = scheduleTypeRepository.findByIdOrNull(scheduleTypeId)
            ?: throw BusinessException(scheduleTypeId, "ScheduleTypeId", ErrorCode.SCHEDULE_NOT_FOUND)

        return scheduleRepository.save(
            Schedule(
                name = name,
                startTime = startTime,
                endTime = endTime,
                scheduleType = findSchedule,
            )
        ).id!!
    }

    fun findAllSchedule(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
    ): List<FindAllScheduleResponse> =
        scheduleRepository.findAllSchedule(startTime, endTime)
            .map { FindAllScheduleResponse.from(it) }
            .toList()
}
