package com.keeper.homepage.domain.game.dto.req

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Positive

const val MAX_BETTING_POINT = 1000L
const val MIN_BETTING_POINT = 10L

data class BaseballStartRequest(
    @field:Positive
    @field:Max(MAX_BETTING_POINT)
    @field:Min(MIN_BETTING_POINT)
    val bettingPoint: Int,
)
