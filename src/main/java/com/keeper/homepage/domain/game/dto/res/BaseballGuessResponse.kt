package com.keeper.homepage.domain.game.dto.res

import com.keeper.homepage.domain.game.entity.redis.BaseballResultEntity

data class BaseballGuessResponse(
    val result: List<GuessResultResponse?>,
    val earnablePoints: Int,
) {
    data class GuessResultResponse(val guessNumber: String, val strike: Int, val ball: Int) {
        constructor(guessResultEntity: BaseballResultEntity.GuessResultEntity) : this(
            guessNumber = guessResultEntity.guessNumber,
            strike = guessResultEntity.strike,
            ball = guessResultEntity.ball,
        )
    }
}
