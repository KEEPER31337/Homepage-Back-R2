package com.keeper.homepage.domain.game.entity.redis

import com.keeper.homepage.domain.game.application.GUESS_NUMBER_LENGTH
import com.keeper.homepage.domain.game.application.TRY_COUNT
import com.keeper.homepage.domain.game.support.BaseballSupport
import java.time.LocalDateTime

const val SECOND_PER_GAME = 30 // 시간제한: 30s

class BaseballResultEntity(
        val correctNumber: String,
        val bettingPoint: Int,
        val results: MutableList<GuessResultEntity?> = mutableListOf(),
        var earnablePoint: Int,
) {
    var lastGuessTime: LocalDateTime = LocalDateTime.now()

    fun updateTimeoutGames(): Int {
        if (this.isEnd()) return 0
        val (passedGameCount, remainedSeconds) = BaseballSupport.getPassedGameCount(results.size, lastGuessTime)
        (1..passedGameCount).forEach { _ -> if (results.size < TRY_COUNT) results.add(null) }
        updateEarnablePointByTimeoutGames(passedGameCount)
        lastGuessTime = lastGuessTime.plusSeconds((passedGameCount * SECOND_PER_GAME).toLong())
        return remainedSeconds
    }

    private fun updateEarnablePointByTimeoutGames(passedGameCount: Int) {
        (1..passedGameCount).forEach { _ ->
            // TODO: 포인트 획득 전략 정해지면 다시 구현
            this.earnablePoint -= this.earnablePoint / 10
        }
    }

    fun update(guessNumber: String) {
        if (isAlreadyCorrect()) {
            return
        }
        updateTimeoutGames()
        if (results.size >= TRY_COUNT) {
            return
        }
        lastGuessTime = LocalDateTime.now()
        val result = BaseballSupport.guessAndGetResult(correctNumber, guessNumber)
        updateEarnedPointsByResult(result)
        results.add(result)
    }

    fun isEnd(): Boolean {
        return this.results.size >= TRY_COUNT || this.isAlreadyCorrect()
    }

    fun isAlreadyCorrect(): Boolean {
        return results.isNotEmpty() && results.last() != null && results.last()!!.isCorrect()
    }

    private fun updateEarnedPointsByResult(result: GuessResultEntity) {
        if (result.isCorrect()) {
            return
        }
        this.earnablePoint -= this.earnablePoint / 10
    }

    data class GuessResultEntity(val guessNumber: String, val strike: Int, val ball: Int) {
        fun isCorrect() = this.strike == GUESS_NUMBER_LENGTH
    }
}
