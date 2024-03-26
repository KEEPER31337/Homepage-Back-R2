package com.keeper.homepage.domain.calendar.api

import com.keeper.homepage.domain.calendar.application.ScheduleService
import com.keeper.homepage.domain.calendar.dto.request.SaveScheduleRequest
import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/schedule")
class ScheduleController(
    val scheduleService: ScheduleService,
) {

    @PostMapping
    fun saveSchedule(@RequestBody @Valid saveScheduleRequest: SaveScheduleRequest): ResponseEntity<Void> {
        scheduleService.saveSchedule(
            saveScheduleRequest.name,
            saveScheduleRequest.startTime,
            saveScheduleRequest.endTime,
            saveScheduleRequest.scheduleTypeId.toLong()
        )

        return ResponseEntity.ok().build()
    }

}