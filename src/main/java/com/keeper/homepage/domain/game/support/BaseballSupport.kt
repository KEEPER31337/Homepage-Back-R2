package com.keeper.homepage.domain.game.support

import com.keeper.homepage.domain.game.application.GUESS_NUMBER_LENGTH
import com.keeper.homepage.domain.game.application.TRY_COUNT
import com.keeper.homepage.domain.game.dto.BaseballResult
import com.keeper.homepage.domain.game.dto.SECOND_PER_GAME
import java.time.LocalDateTime
import java.time.ZoneOffset

class BaseballSupport {
    companion object {
        fun updateTimeoutGames(results: MutableList<BaseballResult.StrikeBall?>, lastGuessTime: LocalDateTime) {
            val now = LocalDateTime.now()
            val passedSecond =
                now.toEpochSecond(ZoneOffset.UTC) - lastGuessTime.toEpochSecond(ZoneOffset.UTC) - 1 // RTT 넉넉하게 1s
            // 마지막으로 플레이 한 시간이 너무 오래 지나 list에 너무 많은 값이 들어가는걸 방지
            println("${now.toEpochSecond(ZoneOffset.UTC)}, ${lastGuessTime.toEpochSecond(ZoneOffset.UTC)}, $passedSecond")
            val passedGameCount =
                if (passedSecond / SECOND_PER_GAME < TRY_COUNT) (passedSecond / SECOND_PER_GAME).toInt() else TRY_COUNT
            if (passedSecond > SECOND_PER_GAME) {
                (1..passedGameCount).forEach { _ -> results.add(null) }
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
