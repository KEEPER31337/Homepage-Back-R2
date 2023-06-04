package com.keeper.homepage.domain.game.dto.req

import com.keeper.homepage.domain.game.dto.BaseballResult

data class BaseballGuessResponse(
    val result: List<BaseballResult.GuessResult?>,
    val earnedPoint: Int,
) {
    companion object {
        val EMPTY = BaseballGuessResponse(listOf(), 0)
    }
}
