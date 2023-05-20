package com.keeper.homepage.domain.game.dto.req

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Positive

const val MAX_TITLE_LENGTH = 10000L

data class BaseballStartRequest(
    @field:Positive
    @field:Max(MAX_TITLE_LENGTH)
    val bettingPoint: Long,
)
