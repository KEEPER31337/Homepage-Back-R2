package com.keeper.homepage.domain.game.dto

import com.keeper.homepage.domain.game.application.GUESS_NUMBER_LENGTH
import com.keeper.homepage.domain.game.application.TRY_COUNT
import com.keeper.homepage.domain.game.support.BaseballSupport
import java.time.LocalDateTime

const val SECOND_PER_GAME = 30 // 시간제한: 30s

class BaseballResult(
    val correctNumber: String,
    val bettingPoint: Int,
    val results: MutableList<StrikeBall?> = mutableListOf()
) {
    var lastGuessTime: LocalDateTime = LocalDateTime.now()

    fun updateTimeoutGames() = BaseballSupport.updateTimeoutGames(results, lastGuessTime)

    /**
     * @return: 4스트라이크나 timeout으로 게임이 끝났는지 여부
     */
    fun update(guessNumber: String): End {
        updateTimeoutGames()
        lastGuessTime = LocalDateTime.now()
        if (results.size >= TRY_COUNT) {
            return End.TIMEOUT
        }
        BaseballSupport.updateResults(results, correctNumber, guessNumber)
        return End.get(results.last())
    }

    data class StrikeBall(val strike: Int, val ball: Int)
    enum class End {
        CORRECT {
            override fun getEarnedPoint(bettingPoint: Int): Int {
                // TODO 정답 시 포인트 부여 어떻게 할 지 기획 나오면 다시 로직 작성
                return bettingPoint * 2
            }
        },
        TIMEOUT {
            override fun getEarnedPoint(bettingPoint: Int): Int = 0
        },
        MISMATCH {
            override fun getEarnedPoint(bettingPoint: Int): Int = 0
        };

        abstract fun getEarnedPoint(bettingPoint: Int): Int

        companion object {
            fun get(last: StrikeBall?): End =
                if (last == null) TIMEOUT
                else if (last.strike == GUESS_NUMBER_LENGTH) CORRECT
                else MISMATCH
        }
    }
}
