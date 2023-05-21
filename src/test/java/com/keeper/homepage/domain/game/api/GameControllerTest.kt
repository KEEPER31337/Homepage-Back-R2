package com.keeper.homepage.domain.game.api

import com.keeper.homepage.domain.game.application.GUESS_NUMBER_LENGTH
import com.keeper.homepage.domain.game.application.REDIS_KEY_PREFIX
import com.keeper.homepage.domain.game.dto.BaseballResult
import com.keeper.homepage.domain.game.dto.BaseballResult.StrikeBall
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
        fun `야구게임을 오늘 했으면 true 아니면 false가 나와야 한다`() {
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

            gameRepository.findAllByMember(player)
                .get()
                .baseball
                .increaseBaseballTimes()

            callBaseballIsAlreadyPlayed()
                .andExpect(status().isOk)
                .andExpect(jsonPath("$").value(true))
        }

        @Test
        fun `베팅 포인트가 양수면 게임이 시작된다`() {
            player.addPoint(1000)
            memberRepository.save(player)

            val result = callBaseballStart(1000)
                .andExpect(status().isNoContent)

            val baseball = gameRepository.findAllByMember(player).get().baseball
            assertThat(baseball.isAlreadyPlayed).isTrue() // 게임을 시작하면 play count가 증가한다.
            val data = redisUtil.getData(REDIS_KEY_PREFIX + player.id.toString(), BaseballResult::class.java)
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
                        fieldWithPath("bettingPoint").description("베팅을 할 포인트"),
                    ),
                )
            )
        }

        @Test
        fun `베팅 포인트가 음수거나 0이면 게임을 플레이 할 수 없다`() {
            player.addPoint(1000)
            memberRepository.save(player)
            callBaseballStart(-1000)
                .andExpect(status().isBadRequest)
            callBaseballStart(0)
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
            baseballService.initWhenNotExistGameMemberInfo(player)

            val result = callBaseballGuess(
                guessNumber = "1234", correctNumber = "1234", bettingPoint = 1000,
                results = mutableListOf(StrikeBall(0, 0), StrikeBall(2, 2), StrikeBall(3, 0))
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.guessNumber").value("1234"))
                .andExpect(jsonPath("$.result").isArray)
                .andExpect(jsonPath("$.result[0]").exists())
                .andExpect(jsonPath("$.earnedPoint").isNumber)

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
                        fieldWithPath("guessNumber").description("사용자가 입력한 추측 숫자"),
                        fieldWithPath("result").description("타임아웃난 round는 null"),
                        fieldWithPath("result[].strike").description("strike"),
                        fieldWithPath("result[].ball").description("ball"),
                        fieldWithPath("earnedPoint").description("획득한 포인트 (마지막 게임이 아니면 0)"),
                    ),
                )
            )
        }

        @Test
        fun `guessNumber가 4자가 아니면 guess는 실패한다`() {
            baseballService.initWhenNotExistGameMemberInfo(player)
            callBaseballGuess(guessNumber = "123", correctNumber = "1234", bettingPoint = 1000)
                .andExpect(status().isBadRequest)
            callBaseballGuess(guessNumber = "12345", correctNumber = "1234", bettingPoint = 1000)
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `맞췄으면 guess는 포인트를 부여해야 한다`() {
            baseballService.initWhenNotExistGameMemberInfo(player)
            val beforePlayerPoint = player.point

            callBaseballGuess(
                guessNumber = "1234", correctNumber = "1234", bettingPoint = 1000,
                results = mutableListOf(StrikeBall(2, 2), null, StrikeBall(3, 0))
            ).andExpect(status().isOk)

            assertThat(beforePlayerPoint + 2000).isEqualTo(player.point)
        }

        @Test
        fun `이미 맞췄으면 guess는 더이상 포인트 부여를 하지 않는다`() {
            baseballService.initWhenNotExistGameMemberInfo(player)
            val beforePlayerPoint = player.point

            callBaseballGuess(
                guessNumber = "1234", correctNumber = "1234", bettingPoint = 1000,
                results = mutableListOf(StrikeBall(2, 2), null, StrikeBall(4, 0))
            ).andExpect(status().isOk)

            assertThat(beforePlayerPoint).isEqualTo(player.point)
        }

        @Test
        fun `시도 회수를 초과했을 경우 guess는 더이상 포인트 부여를 하지 않는다`() {
            baseballService.initWhenNotExistGameMemberInfo(player)
            val beforePlayerPoint = player.point

            callBaseballGuess(
                guessNumber = "1234", correctNumber = "1234", bettingPoint = 1000,
                results = mutableListOf(
                    StrikeBall(2, 2), null, StrikeBall(4, 0),
                    null, null, null, null, null, null
                )
            ).andExpect(status().isOk)

            assertThat(beforePlayerPoint).isEqualTo(player.point)
        }
    }
}
