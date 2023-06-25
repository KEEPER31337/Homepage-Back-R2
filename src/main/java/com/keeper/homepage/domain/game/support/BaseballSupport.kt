package com.keeper.homepage.domain.game.support

import com.keeper.homepage.domain.game.application.GUESS_NUMBER_LENGTH
import com.keeper.homepage.domain.game.application.TRY_COUNT
import com.keeper.homepage.domain.game.dto.BaseballResult
import com.keeper.homepage.domain.game.dto.SECOND_PER_GAME
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

class BaseballSupport {
    companion object {
        fun getPassedGameCount(
            playedRoundCount: Int,
            lastGuessTime: LocalDateTime,
        ): Int {
            val now = LocalDateTime.now()
            val passedSecond = now.toEpochSecond(UTC) - lastGuessTime.toEpochSecond(UTC)
            // 마지막으로 플레이 한 시간이 너무 오래 지나 list에 너무 많은 값이 들어가는걸 방지
            val passedGameCount = passedSecond / SECOND_PER_GAME
            return if (passedGameCount < TRY_COUNT - playedRoundCount) {
                passedGameCount.toInt()
            } else {
                TRY_COUNT - playedRoundCount
            }
        }

        fun guessAndGetResult(
            correctNumber: String,
            guessNumber: String
        ): BaseballResult.GuessResult {
            if (guessNumber.length != GUESS_NUMBER_LENGTH) {
                return BaseballResult.GuessResult(guessNumber, 0, 0)
            }
            return guess(correctNumber, guessNumber)
        }

        private fun guess(correctNumber: String, guessNumber: String): BaseballResult.GuessResult {
            var strike = 0
            var ball = 0
            for (i in guessNumber.indices) {
                if (correctNumber[i] == guessNumber[i]) strike++
                else if (correctNumber.contains(guessNumber[i])) ball++
            }
            return BaseballResult.GuessResult(guessNumber, strike, ball)
        }
    }
}
