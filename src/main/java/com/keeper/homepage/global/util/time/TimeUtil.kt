package com.keeper.homepage.global.util.time

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs

infix fun LocalDateTime.isNotBefore(beforeTime: LocalDateTime): Boolean = this.isAfter(beforeTime)

infix fun LocalDateTime.diff(beforeTime: LocalDateTime): Long = abs(ChronoUnit.DAYS.between(beforeTime, this))
