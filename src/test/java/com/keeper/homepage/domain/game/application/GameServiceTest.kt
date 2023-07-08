package com.keeper.homepage.domain.game.application

import com.keeper.homepage.IntegrationTest
import com.keeper.homepage.domain.game.GameTestHelper
import com.keeper.homepage.domain.game.dto.res.GameRankResponse
import com.keeper.homepage.domain.game.entity.Game
import com.keeper.homepage.domain.game.entity.embedded.Baseball
import com.keeper.homepage.domain.member.entity.Member
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import java.time.LocalDate
import java.time.LocalDateTime

fun GameTestHelper.generate(
    member: Member,
    baseballDayPoint: Int,
    lastPlayTime: LocalDateTime = LocalDateTime.now()
): Game {
    return this.builder().member(member)
        .baseball(Baseball.from(1, baseballDayPoint))
        .lastPlayTime(lastPlayTime)
        .build()
}

class GameServiceTest : IntegrationTest() {

    @Test
    fun getGameRanks() {
        val members = (1..6).map { _ -> memberTestHelper.generate() }
        val now = LocalDateTime.now()
        val game1 = gameTestHelper.generate(members[0], 1000, now)
        val game2 = gameTestHelper.generate(members[1], 1500, now) // 획득 포인트가 같으면 마지막 플레이 타임이 앞서는 사람이 높은 순위이다.
        val game3 = gameTestHelper.generate(members[2], 1500, now.plusMinutes(10))
        val game4 = gameTestHelper.generate(members[3], 2000, now)
        val game5 = gameTestHelper.generate(members[4], 500, now)
        val game6 = gameTestHelper.generate(members[5], 0, now)
        given(gameFindService.findAllByPlayDate(LocalDate.now()))
            .willReturn(listOf(game1, game2, game3, game4, game5, game6))

        val gameRanks = gameService.getGameRanks()

        assertThat(gameRanks).hasSize(4)
        assertThat(gameRanks).containsExactly(
            GameRankResponse(1, members[3], 2000),
            GameRankResponse(2, members[1], 1500),
            GameRankResponse(3, members[2], 1500),
            GameRankResponse(4, members[0], 1000),
        )
    }
}
