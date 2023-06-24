package com.keeper.homepage.domain.game.support

import com.keeper.homepage.domain.game.dto.BaseballResult.GuessResult
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime
import java.util.stream.Stream

class BaseballSupportTest {
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `timeout 테스트` {
        @Test
        fun `시간안에 플레이 했으면 null인 게임은 없어야 한다`() {
            val results: MutableList<GuessResult?> = mutableListOf()
            val lastGuessTime = LocalDateTime.now().minusSeconds(15)
            BaseballSupport.updateTimeoutGames(results, lastGuessTime)
            assertThat(results).hasSize(0)
        }

        @Test
        fun `34초가 지난 후면 1게임은 null이 되어야 한다`() {
            val results: MutableList<GuessResult?> = mutableListOf()
            val lastGuessTime = LocalDateTime.now().minusSeconds(34)
            BaseballSupport.updateTimeoutGames(results, lastGuessTime)
            assertThat(results).hasSize(1)
            assertThat(results[0]).isNull()
        }

        @Test
        fun `90초가 지난 후면 3게임은 null이 되어야 한다`() {
            val results: MutableList<GuessResult?> = mutableListOf()
            val lastGuessTime = LocalDateTime.now().minusSeconds(90)
            BaseballSupport.updateTimeoutGames(results, lastGuessTime)
            assertThat(results).hasSize(3)
            assertThat(results).containsExactly(null, null, null)
        }

        @Test
        fun `92초가 지난 후면 3게임은 null이 되어야 한다`() {
            val results: MutableList<GuessResult?> = mutableListOf()
            val lastGuessTime = LocalDateTime.now().minusSeconds(92)
            BaseballSupport.updateTimeoutGames(results, lastGuessTime)
            assertThat(results).hasSize(3)
            assertThat(results).containsExactly(null, null, null)
        }

        @Test
        fun `300초가 지난 후면 9게임은 null이 되어야 한다`() {
            val results: MutableList<GuessResult?> = mutableListOf()
            val lastGuessTime = LocalDateTime.now().minusSeconds(300)
            BaseballSupport.updateTimeoutGames(results, lastGuessTime)
            assertThat(results).hasSize(9)
            assertThat(results).containsExactly(null, null, null, null, null, null, null, null, null)
        }

        @Test
        fun `1000초가 지났더라도 9게임만 null이 되어야 한다`() {
            val results: MutableList<GuessResult?> = mutableListOf()
            val lastGuessTime = LocalDateTime.now().minusSeconds(1000)
            BaseballSupport.updateTimeoutGames(results, lastGuessTime)
            assertThat(results).hasSize(9)
            assertThat(results).containsExactly(null, null, null, null, null, null, null, null, null)
        }

        @Test
        fun `기존에 플레이 하던 게임에서 1000초가 지났더라도 나머지 게임만 null이 되어야 한다`() {
            val results: MutableList<GuessResult?> = mutableListOf(GuessResult("1234", 1, 2), GuessResult("5678", 3, 1))
            val lastGuessTime = LocalDateTime.now().minusSeconds(1000)
            BaseballSupport.updateTimeoutGames(results, lastGuessTime)
            assertThat(results).hasSize(9)
            assertThat(results).containsExactly(
                GuessResult("1234", 1, 2),
                GuessResult("5678", 3, 1),
                null, null, null, null,
                null, null, null
            )
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `guess 테스트` {
        @ParameterizedTest
        @MethodSource
        fun `시간안에 플레이 했으면 null인 게임은 없어야 한다`(
            correctNumber: String,
            guessNumber: String,
            expectedStrike: Int,
            expectedBall: Int
        ) {
            val results: MutableList<GuessResult?> = mutableListOf()
            BaseballSupport.updateResults(results, correctNumber, guessNumber)
            assertThat(results).hasSize(1)
            assertThat(results.last()!!.strike).isEqualTo(expectedStrike)
            assertThat(results.last()!!.ball).isEqualTo(expectedBall)
        }

        fun `시간안에 플레이 했으면 null인 게임은 없어야 한다`() = Stream.of(
            Arguments.arguments("1234", "1234", 4, 0),
            Arguments.arguments("1234", "1256", 2, 0),
            Arguments.arguments("1234", "4321", 0, 4),
            Arguments.arguments("1234", "1432", 2, 2),
            Arguments.arguments("1234", "9328", 0, 2),
            Arguments.arguments("1234", "1423", 1, 3),
            Arguments.arguments("1234", "5678", 0, 0),
            Arguments.arguments("1234", "1567", 1, 0),
        )

        @Test
        fun `guessNumber 길이가 4가 아니면 무의미한 결과를 줘야 한다`() {
            val results: MutableList<GuessResult?> = mutableListOf()
            BaseballSupport.updateResults(results, "1234", "456")
            assertThat(results).hasSize(1)
            assertThat(results.last()!!.strike).isEqualTo(0)
            assertThat(results.last()!!.ball).isEqualTo(0)
        }
    }
}
