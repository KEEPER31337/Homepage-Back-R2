package com.keeper.homepage.domain.game.dto.res

import com.keeper.homepage.domain.game.dto.BaseballResult

data class BaseballGuessResponse(
    val result: List<BaseballResult.GuessResult?>,
    val earnablePoints: Int,
) {
    companion object {
        val EMPTY = BaseballGuessResponse(listOf(), 0)
    }
}
