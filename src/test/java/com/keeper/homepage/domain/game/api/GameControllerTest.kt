package com.keeper.homepage.domain.game.api

import com.keeper.homepage.domain.game.application.GUESS_NUMBER_LENGTH
import com.keeper.homepage.domain.game.application.MAX_BETTING_POINT
import com.keeper.homepage.domain.game.application.MIN_BETTING_POINT
import com.keeper.homepage.domain.game.application.REDIS_KEY_PREFIX
import com.keeper.homepage.domain.game.entity.redis.BaseballResultEntity
import com.keeper.homepage.domain.game.entity.redis.BaseballResultEntity.GuessResultEntity
import com.keeper.homepage.global.config.security.data.JwtType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName
import org.springframework.restdocs.cookies.CookieDocumentation.requestCookies
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

class GameControllerTest : GameApiTestHelper() {

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `야구 게임` {
        @Test
        fun `야구게임 정보를 불러온다`() {
            callBaseballGameInfo()
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.guessNumberLength").value(4))
                .andExpect(jsonPath("$.tryCount").value(9))
                .andExpect(jsonPath("$.maxBettingPoint").value(1000L))
                .andExpect(jsonPath("$.minBettingPoint").value(10L))
                .andDo(
                    document(
                        "baseball-game-info",
                        requestCookies(
                            cookieWithName(JwtType.ACCESS_TOKEN.tokenName).description("ACCESS TOKEN"),
                            cookieWithName(JwtType.REFRESH_TOKEN.tokenName).description("REFRESH TOKEN")
                        ),
                        responseFields(
                            fieldWithPath("guessNumberLength").description("추측할 숫자 길이"),
                            fieldWithPath("tryCount").description("라운드 수"),
                            fieldWithPath("maxBettingPoint").description("최대 베팅 포인트"),
                            fieldWithPath("minBettingPoint").description("최소 베팅 포인트"),

                            )
                    )
                )
        }

        @Test
        fun `야구게임을 오늘 안했으면 false가 나와야 한다`() {
            callBaseballIsAlreadyPlayed()
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$").value(false))
                .andDo(
                    document(
                        "baseball-is-already-played",
                        requestCookies(
                            cookieWithName(JwtType.ACCESS_TOKEN.tokenName).description("ACCESS TOKEN"),
                            cookieWithName(JwtType.REFRESH_TOKEN.tokenName).description("REFRESH TOKEN")
                        ),
                    )
                )
        }

        @Test
        fun `야구게임을 오늘 했으면 true가 나와야 한다`() {
            gameStart()

            callBaseballIsAlreadyPlayed()
                .andExpect(status().isOk)
                .andExpect(jsonPath("$").value(true))
        }

        @Test
        fun `베팅 포인트가 10포인트 이상, 1000포인트 이하면 게임이 시작된다`() {
            player.addPoint(1000)
            memberRepository.save(player)

            val result = callBaseballStart(1000)
                .andExpect(status().isNoContent)

            val baseball = gameFindService.findByMemberOrInit(player).baseball
            assertThat(baseball.isAlreadyPlayed).isTrue() // 게임을 시작하면 play count가 증가한다.
            val data = redisUtil.getData(REDIS_KEY_PREFIX + player.id.toString(), BaseballResultEntity::class.java)
            assertThat(data).isNotEmpty
            assertThat(data.get().correctNumber).hasSize(4)
            assertThat(data.get().correctNumber).containsOnlyDigits()
            assertThat(data.get().bettingPoint).isEqualTo(1000)
            assertThat(data.get().results).isEmpty()
            assertThat(data.get().lastGuessTime).isBefore(LocalDateTime.now())

            result.andDo(
                document(
                    "baseball-start",
                    requestCookies(
                        cookieWithName(JwtType.ACCESS_TOKEN.tokenName).description("ACCESS TOKEN"),
                        cookieWithName(JwtType.REFRESH_TOKEN.tokenName).description("REFRESH TOKEN")
                    ),
                    requestFields(
                        fieldWithPath("bettingPoint").description("베팅을 할 포인트 (${MIN_BETTING_POINT}이상 ${MAX_BETTING_POINT}이하)"),
                    ),
                )
            )
        }

        @Test
        fun `베팅 포인트가 1000포인트보다 크면 게임을 플레이할 수 없다`() {
            player.addPoint(1000)
            memberRepository.save(player)
            callBaseballStart(1001)
                .andExpect(status().isBadRequest)
            callBaseballStart(2000)
                .andExpect(status().isBadRequest)
            @Suppress("INTEGER_OVERFLOW")
            callBaseballStart(Int.MAX_VALUE + 1)
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `베팅 포인트가 음수거나 10포인트보다 작으면 게임을 플레이 할 수 없다`() {
            player.addPoint(1000)
            memberRepository.save(player)
            callBaseballStart(-1000)
                .andExpect(status().isBadRequest)
            callBaseballStart(0)
                .andExpect(status().isBadRequest)
            callBaseballStart(9)
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `베팅 포인트가 가지고 있는 포인트보다 적으면 플레이 할 수 없다`() {
            player.addPoint(1000)
            memberRepository.save(player)
            callBaseballStart(2000)
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `valid한 request면 guess는 성공해야 한다`() {
            val result = callBaseballGuess(
                guessNumber = "1234", correctNumber = "1234", bettingPoint = 1000,
                results = mutableListOf(
                    GuessResultEntity("1357", 0, 0),
                    GuessResultEntity("2468", 2, 2),
                    GuessResultEntity("7890", 3, 0)
                )
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.results").isArray)
                .andExpect(jsonPath("$.results[0]").exists())
                .andExpect(jsonPath("$.earnablePoints").isNumber)

            result.andDo(
                document(
                    "baseball-guess",
                    requestCookies(
                        cookieWithName(JwtType.ACCESS_TOKEN.tokenName).description("ACCESS TOKEN"),
                        cookieWithName(JwtType.REFRESH_TOKEN.tokenName).description("REFRESH TOKEN")
                    ),
                    requestFields(
                        fieldWithPath("guessNumber").description("추측한 숫자 (반드시 ${GUESS_NUMBER_LENGTH}자 여야 합니다)"),
                    ),
                    responseFields(
                        fieldWithPath("results").description("타임아웃난 round는 null"),
                        fieldWithPath("results[].guessNumber").description("해당 라운드에 사용자가 입력한 추측 숫자"),
                        fieldWithPath("results[].strike").description("strike"),
                        fieldWithPath("results[].ball").description("ball"),
                        fieldWithPath("earnablePoints").description("획득한 포인트 (마지막 게임이 아니면 0)"),
                    ),
                )
            )
        }

        @Test
        fun `guessNumber가 4자가 아니면 guess는 실패한다`() {
            callBaseballGuess(guessNumber = "123", correctNumber = "1234", bettingPoint = 1000)
                .andExpect(status().isBadRequest)
            callBaseballGuess(guessNumber = "12345", correctNumber = "1234", bettingPoint = 1000)
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `맞췄으면 guess는 earnablePoints만큼 포인트를 부여해야 한다`() {
            val beforePlayerPoint = player.point
            val earnablePoints = 3000

            callBaseballGuess(
                guessNumber = "1234", correctNumber = "1234", bettingPoint = 1000, earnablePoints = earnablePoints,
                results = mutableListOf(GuessResultEntity("1234", 2, 2), null, GuessResultEntity("5678", 3, 0))
            ).andExpect(status().isOk)

            assertThat(beforePlayerPoint + earnablePoints).isEqualTo(player.point)
        }

        @Test
        fun `이미 맞췄으면 guess는 더이상 포인트 부여를 하지 않는다`() {
            val beforePlayerPoint = player.point

            callBaseballGuess(
                guessNumber = "1234", correctNumber = "1234", bettingPoint = 1000,
                results = mutableListOf(GuessResultEntity("1234", 2, 2), null, GuessResultEntity("5678", 4, 0))
            ).andExpect(status().isOk)

            assertThat(beforePlayerPoint).isEqualTo(player.point)
        }

        @Test
        fun `시도 회수를 초과했을 경우 guess는 더이상 포인트 부여를 하지 않는다`() {
            val beforePlayerPoint = player.point

            callBaseballGuess(
                guessNumber = "1234", correctNumber = "1234", bettingPoint = 1000,
                results = mutableListOf(
                    GuessResultEntity("1234", 2, 2), null, GuessResultEntity("5678", 4, 0),
                    null, null, null, null, null, null
                )
            ).andExpect(status().isOk)

            assertThat(beforePlayerPoint).isEqualTo(player.point)
        }

        @Test
        fun `플레이 한 결과를 보여주어야 한다`() {
            gameStart()

            callGetBaseballResult(
                results = mutableListOf(
                    GuessResultEntity("1234", 2, 2),
                    null, GuessResultEntity("3456", 1, 0),
                    null, null, GuessResultEntity("5678", 3, 0)
                )
            ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk)
                .andExpect(
                    jsonPath("$.results[0]").value(
                        linkedMapOf(
                            "guessNumber" to "1234",
                            "strike" to 2,
                            "ball" to 2
                        )
                    )
                )
                .andExpect(jsonPath("$.results[1]").value(null))
                .andExpect(
                    jsonPath("$.results[2]").value(
                        linkedMapOf(
                            "guessNumber" to "3456",
                            "strike" to 1,
                            "ball" to 0
                        )
                    )
                )
                .andExpect(jsonPath("$.results[3]").value(null))
                .andExpect(jsonPath("$.results[4]").value(null))
                .andExpect(
                    jsonPath("$.results[5]").value(
                        linkedMapOf(
                            "guessNumber" to "5678",
                            "strike" to 3,
                            "ball" to 0
                        )
                    )
                )
                .andExpect(jsonPath("$.earnablePoints").value(0))
                .andDo(
                    document(
                        "get-baseball-results",
                        requestCookies(
                            cookieWithName(JwtType.ACCESS_TOKEN.tokenName).description("ACCESS TOKEN"),
                            cookieWithName(JwtType.REFRESH_TOKEN.tokenName).description("REFRESH TOKEN")
                        ),
                        responseFields(
                            subsectionWithPath("results").description("타임아웃난 round는 null"),
                            fieldWithPath("earnablePoints").description("획득한 포인트 (오늘 끝낸 게임이 아니면 0)"),
                        ),
                    )
                )
        }
    }
}
