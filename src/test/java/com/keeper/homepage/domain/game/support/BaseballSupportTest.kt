package com.keeper.homepage.domain.game.support

import com.keeper.homepage.domain.game.entity.redis.BaseballResultEntity.GuessResultEntity
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
        fun `시간안에 플레이 했으면 pass한 게임은 없어야 한다`() {
            val lastGuessTime = LocalDateTime.now().minusSeconds(15)
            val passedGameCount = BaseballSupport.getPassedGameCount(0, lastGuessTime)
            assertThat(passedGameCount).isEqualTo(0)
        }

        @Test
        fun `34초가 지난 후면 1게임은 pass한 게임이어야 한다`() {
            val lastGuessTime = LocalDateTime.now().minusSeconds(34)
            val passedGameCount = BaseballSupport.getPassedGameCount(0, lastGuessTime)
            assertThat(passedGameCount).isEqualTo(1)
        }

        @Test
        fun `90초가 지난 후면 3게임은 pass한 게임이어야 한다`() {
            val lastGuessTime = LocalDateTime.now().minusSeconds(90)
            val passedGameCount = BaseballSupport.getPassedGameCount(0, lastGuessTime)
            assertThat(passedGameCount).isEqualTo(3)
        }

        @Test
        fun `92초가 지난 후면 3게임은 pass한 게임이어야 한다`() {
            val lastGuessTime = LocalDateTime.now().minusSeconds(92)
            val passedGameCount = BaseballSupport.getPassedGameCount(0, lastGuessTime)
            assertThat(passedGameCount).isEqualTo(3)
        }

        @Test
        fun `300초가 지난 후면 9게임은 pass한 게임이어야 한다`() {
            val lastGuessTime = LocalDateTime.now().minusSeconds(300)
            val passedGameCount = BaseballSupport.getPassedGameCount(0, lastGuessTime)
            assertThat(passedGameCount).isEqualTo(9)
        }

        @Test
        fun `1000초가 지났더라도 9게임만 pass한 게임이어야 한다`() {
            val lastGuessTime = LocalDateTime.now().minusSeconds(1000)
            val passedGameCount = BaseballSupport.getPassedGameCount(0, lastGuessTime)
            assertThat(passedGameCount).isEqualTo(9)
        }

        @Test
        fun `2게임을 플레이했으면 1000초가 지났더라도 7 게임만 pass 되어야 한다`() {
            val results: MutableList<GuessResultEntity?> = mutableListOf(GuessResultEntity("1234", 1, 2), GuessResultEntity("5678", 3, 1))
            val lastGuessTime = LocalDateTime.now().minusSeconds(1000)
            val passedGameCount = BaseballSupport.getPassedGameCount(results.size, lastGuessTime)
            assertThat(passedGameCount).isEqualTo(7)
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `guess 테스트` {
        @ParameterizedTest
        @MethodSource
        fun `같은 숫자면 ball, 같은 위치이면 strike를 올려야 한다`(
            correctNumber: String,
            guessNumber: String,
            expectedStrike: Int,
            expectedBall: Int
        ) {
            val result = BaseballSupport.guessAndGetResult(correctNumber, guessNumber)
            assertThat(result.strike).isEqualTo(expectedStrike)
            assertThat(result.ball).isEqualTo(expectedBall)
        }

        fun `같은 숫자면 ball, 같은 위치이면 strike를 올려야 한다`() = Stream.of(
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
            val result = BaseballSupport.guessAndGetResult("1234", "456")
            assertThat(result.strike).isEqualTo(0)
            assertThat(result.ball).isEqualTo(0)
        }
    }
}
