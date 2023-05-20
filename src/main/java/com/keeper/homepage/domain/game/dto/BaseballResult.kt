package com.keeper.homepage.domain.game.dto

import java.time.LocalDateTime

class BaseballResult(
    val bettingPoint: Long,
    val results: MutableList<StrikeBall> = mutableListOf()
) {

    var finalGetPoint: Long = 0
    var lastGuessTime: LocalDateTime = LocalDateTime.now()

    class StrikeBall(val strike: Int, val ball: Int)
}
