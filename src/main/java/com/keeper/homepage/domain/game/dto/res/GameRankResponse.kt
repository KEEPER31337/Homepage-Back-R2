package com.keeper.homepage.domain.game.dto.res

import com.keeper.homepage.domain.member.entity.Member

data class GameRankResponse(
    val rank: Int,
    val nickname: String,
    val generation: String,
    val todayEarnedPoint: Int,
) {
    constructor(rank: Int, member: Member, todayEarnedPoint: Int) : this(
        rank = rank,
        nickname = member.nickname,
        generation = member.generation.toString(),
        todayEarnedPoint = todayEarnedPoint
    )
}
