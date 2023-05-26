package com.keeper.homepage.domain.game.support

import com.keeper.homepage.domain.game.application.GUESS_NUMBER_LENGTH
import com.keeper.homepage.domain.game.application.TRY_COUNT
import com.keeper.homepage.domain.game.dto.BaseballResult
import com.keeper.homepage.domain.game.dto.SECOND_PER_GAME
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

const val RTT_SECOND = 1 // RTT 넉넉하게 1s

class BaseballSupport {
    companion object {
        fun updateTimeoutGames(
            results: MutableList<BaseballResult.StrikeBall?>,
            lastGuessTime: LocalDateTime,
        ) {
            val now = LocalDateTime.now()
            val passedSecond = now.toEpochSecond(UTC) - lastGuessTime.toEpochSecond(UTC) - RTT_SECOND
            // 마지막으로 플레이 한 시간이 너무 오래 지나 list에 너무 많은 값이 들어가는걸 방지
            val passedGameCount = passedSecond / SECOND_PER_GAME
            val finallyPassedGameCount = if (passedGameCount < TRY_COUNT) passedGameCount.toInt() else TRY_COUNT
            if (passedSecond > SECOND_PER_GAME) {
                (1..finallyPassedGameCount).forEach { _ -> if (results.size < TRY_COUNT) results.add(null) }
            }
        }

        fun updateResults(
            results: MutableList<BaseballResult.StrikeBall?>,
            correctNumber: String,
            guessNumber: String
        ) {
            if (guessNumber.length != GUESS_NUMBER_LENGTH) {
                results.add(BaseballResult.StrikeBall(0, 0))
                return
            }
            results.add(guess(correctNumber, guessNumber))
        }

        private fun guess(correctNumber: String, guessNumber: String): BaseballResult.StrikeBall {
            var strike = 0
            var ball = 0
            for (i in guessNumber.indices) {
                if (correctNumber[i] == guessNumber[i]) strike++
                else if (correctNumber.contains(guessNumber[i])) ball++
            }
            return BaseballResult.StrikeBall(strike, ball)
        }
    }
}
