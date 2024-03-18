package com.keeper.homepage.domain.game.dto.req

import com.keeper.homepage.domain.game.application.MAX_BETTING_POINT
import com.keeper.homepage.domain.game.application.MIN_BETTING_POINT
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Positive

data class BaseballStartRequest(
        @field:Positive
        @field:Max(MAX_BETTING_POINT)
        @field:Min(MIN_BETTING_POINT)
        val bettingPoint: Int,
)
