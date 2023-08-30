package com.keeper.homepage.domain.game.application

import com.keeper.homepage.domain.game.dto.res.GameInfoByMemberResponse
import com.keeper.homepage.domain.game.dto.res.GameRankResponse
import com.keeper.homepage.domain.game.entity.Game
import com.keeper.homepage.domain.member.entity.Member
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

const val MAX_RANK_COUNT = 4

fun <T> List<T>.safeSubList(fromIndex: Int, toIndex: Int): List<T> =
    this.subList(fromIndex.coerceAtLeast(0), toIndex.coerceAtMost(this.size))

@Service
@Transactional(readOnly = true)
class GameService(
    val gameFindService: GameFindService
) {
    fun getGameRanks(): List<GameRankResponse> {
        return gameFindService.findAllByPlayDate(LocalDate.now())
            .sortedWith(compareBy({ -getTodayEarnedPoint(it) }, { it.lastPlayTime }))
            .safeSubList(0, MAX_RANK_COUNT)
            .mapIndexed { rank, game -> GameRankResponse(rank + 1, game.member, getTodayEarnedPoint(game)) }
    }

    private fun getTodayEarnedPoint(game: Game): Int {
        return game.baseball.baseballDayPoint + game.dice.diceDayPoint + game.lotto.lottoDayPoint + game.roulette.rouletteDayPoint
    }

    fun getMyInfo(requestMember: Member): GameInfoByMemberResponse {
        val gameEntity = gameFindService.findByMemberOrInit(requestMember)
        val todayTotalEarnedPoint = gameEntity.lotto.lottoDayPoint + gameEntity.dice.diceDayPoint +
                gameEntity.roulette.rouletteDayPoint + gameEntity.baseball.baseballDayPoint
        return GameInfoByMemberResponse(
            todayTotalEarnedPoint = todayTotalEarnedPoint,
            currentMemberPoint = requestMember.point
        )
    }
}
