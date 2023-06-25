package com.keeper.homepage.domain.game.dto.res

import com.keeper.homepage.domain.game.entity.redis.BaseballResultEntity

data class BaseballGuessResponse(
    val result: List<BaseballResultEntity.GuessResult?>,
    val earnablePoints: Int,
) {
    companion object {
        val EMPTY = BaseballGuessResponse(listOf(), 0)
    }
}
