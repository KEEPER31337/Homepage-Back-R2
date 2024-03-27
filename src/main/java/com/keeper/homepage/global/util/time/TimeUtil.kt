package com.keeper.homepage.global.util.time

import java.time.LocalDateTime

infix fun LocalDateTime.isNotBefore(beforeTime: LocalDateTime): Boolean = this.isAfter(beforeTime)

