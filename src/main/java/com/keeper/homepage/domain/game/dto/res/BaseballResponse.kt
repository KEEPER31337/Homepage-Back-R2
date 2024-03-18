package com.keeper.homepage.domain.game.dto.res

import com.keeper.homepage.domain.game.entity.redis.BaseballResultEntity

data class BaseballResponse(
        val results: List<GuessResultResponse?>,
        val bettingPoint: Int,
        val earnablePoint: Int,
        val remainedSecond: Int, // millis 단위는 버림.
) {
    data class GuessResultResponse(val guessNumber: String, val strike: Int, val ball: Int) {
        constructor(guessResultEntity: BaseballResultEntity.GuessResultEntity) : this(
                guessNumber = guessResultEntity.guessNumber,
                strike = guessResultEntity.strike,
                ball = guessResultEntity.ball,
        )
    }
}
