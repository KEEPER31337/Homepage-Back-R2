package com.keeper.homepage.domain.game.api

import com.keeper.homepage.domain.game.application.REDIS_KEY_PREFIX
import com.keeper.homepage.domain.game.dto.BaseballResult
import com.keeper.homepage.global.config.security.data.JwtType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName
import org.springframework.restdocs.cookies.CookieDocumentation.requestCookies
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
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
            assertThat(data.get().bettingPoint).isEqualTo(1000)
            assertThat(data.get().finalGetPoint).isEqualTo(0)
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
    }
}
