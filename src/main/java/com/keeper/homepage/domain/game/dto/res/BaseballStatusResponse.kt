package com.keeper.homepage.domain.game.dto.res

data class BaseballStatusResponse(
    val status: BaseballStatus,
    val baseballPerDay: Int,
)

enum class BaseballStatus {
    NOT_START,
    PLAYING,
    END,
}
