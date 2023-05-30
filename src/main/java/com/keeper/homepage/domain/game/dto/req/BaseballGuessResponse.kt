package com.keeper.homepage.domain.game.dto.req

import com.keeper.homepage.domain.game.dto.BaseballResult

data class BaseballGuessResponse(
    val guessNumber: String,
    val result: List<BaseballResult.StrikeBall?>,
    val earnedPoint: Int,
) {
    companion object {
        val EMPTY = BaseballGuessResponse("", listOf(), 0)
    }
}
