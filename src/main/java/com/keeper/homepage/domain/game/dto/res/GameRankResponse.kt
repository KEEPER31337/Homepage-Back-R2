package com.keeper.homepage.domain.game.dto.res

import com.keeper.homepage.domain.member.entity.Member

data class GameRankResponse(
    val rank: Int,
    val realName: String,
    val generation: String,
    val todayEarnedPoint: Int,
    val profileImageUrl: String?,
    val memberId: Long,
) {
    constructor(rank: Int, member: Member, todayEarnedPoint: Int) : this(
        rank = rank,
        realName = member.realName,
        generation = member.generation.toString(),
        todayEarnedPoint = todayEarnedPoint,
        profileImageUrl = member.thumbnailPath,
        memberId = member.id,
    )
}
