package com.keeper.homepage.domain.game.dto.res

data class BaseballInfoByMemberResponse(
    val guessNumberLength: Int,
    val tryCount: Int,
    val maxBettingPoint: Long,
    val minBettingPoint: Long,
    val maxPlayTime: Int,
)
