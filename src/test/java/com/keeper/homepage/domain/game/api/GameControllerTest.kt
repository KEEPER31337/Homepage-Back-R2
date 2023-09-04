package com.keeper.homepage.domain.game.api

import com.keeper.homepage.domain.game.application.*
import com.keeper.homepage.domain.game.dto.res.BaseballStatus
import com.keeper.homepage.domain.game.entity.redis.BaseballResultEntity
import com.keeper.homepage.domain.game.entity.redis.BaseballResultEntity.GuessResultEntity
import com.keeper.homepage.domain.game.entity.redis.SECOND_PER_GAME
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
    inner class `게임 정보` {
        @Test
        fun `나의 게임 관련 정보를 불러온다`() {
            callGetMyGameInfo()
                .andExpect(status().isOk)
                .andDo(
                    document(
                        "get-my-game-info",
                        requestCookies(
                            cookieWithName(JwtType.ACCESS_TOKEN.tokenName).description("ACCESS TOKEN"),
                            cookieWithName(JwtType.REFRESH_TOKEN.tokenName).description("REFRESH TOKEN")
                        ),
                        responseFields(
                            fieldWithPath("todayTotalEarnedPoint").description("오늘 게임으로 얻은 총 포인트"),
                            fieldWithPath("currentMemberPoint").description("현재 멤버 보유 포인트"),
                        )
                    )
                )
        }

        @Test
        fun `게임 랭킹을 불러온다`() {
            (1..2).forEach { _ -> gameTestHelper.generate() }

            callGetGameRank()
                .andExpect(status().isOk)
                .andDo(
                    document(
                        "game-rank",
                        requestCookies(
                            cookieWithName(JwtType.ACCESS_TOKEN.tokenName).description("ACCESS TOKEN"),
                            cookieWithName(JwtType.REFRESH_TOKEN.tokenName).description("REFRESH TOKEN")
                        ),
                        responseFields(
                            fieldWithPath("[].rank").description("순위 (1~${MAX_RANK_COUNT})"),
                            fieldWithPath("[].realName").description("실명"),
                            fieldWithPath("[].generation").description("기수"),
                            fieldWithPath("[].todayEarnedPoint").description("오늘 얻은 포인트"),
                            fieldWithPath("[].profileImageUrl").description("프로필 이미지 url (상대경로)"),
                            fieldWithPath("[].memberId").description("회원 id")
                        )
                    )
                )
        }
    }

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
                            fieldWithPath("maxPlayTime").description("최대 플레이 가능 횟수"),
                        )
                    )
                )
        }

        @Test
        fun `야구게임을 오늘 안했으면 NOT_START가 나와야 한다`() {
            callBaseballGetStatus()
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value("NOT_START"))
                .andDo(
                    document(
                        "baseball-get-status",
                        requestCookies(
                            cookieWithName(JwtType.ACCESS_TOKEN.tokenName).description("ACCESS TOKEN"),
                            cookieWithName(JwtType.REFRESH_TOKEN.tokenName).description("REFRESH TOKEN")
                        ),
                        responseFields(
                            fieldWithPath("status").description(
                                BaseballStatus.values().map { it.name } + " 중 하나로 내려갑니다."
                            ),
                            fieldWithPath("baseballPerDay").description("현재 야구게임 플레이 횟수 (시작을 안했으면 0, 했으면 1 이상)"),
                        )
                    )
                )
        }

        @Test
        fun `야구게임을 시작했으면 PLAYING이 나와야 한다`() {
            gameStart()
            baseballService.saveBaseballResultInRedis(
                player.id,
                BaseballResultEntity(
                    "1234",
                    1000,
                    mutableListOf(),
                    2000
                ),
                1,
            )

            callBaseballGetStatus()
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value("PLAYING"))
        }

        @Test
        fun `야구게임을 시작했고, 9판 모두 했으면 END가 나와야 한다`() {
            gameStart()
            baseballService.saveBaseballResultInRedis(
                player.id,
                BaseballResultEntity(
                    "1234",
                    1000,
                    mutableListOf(null, null, null, null, null, null, null, null, null),
                    2000
                ),
                1,
            )

            callBaseballGetStatus()
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value("END"))
        }

        @Test
        fun `야구게임을 시작했고, 4strike를 했으면 END가 나와야 한다`() {
            gameStart()
            baseballService.saveBaseballResultInRedis(
                player.id,
                BaseballResultEntity(
                    "1234",
                    1000,
                    mutableListOf(null, null, GuessResultEntity("1234", 4, 0)),
                    2000
                ),
                1,
            )

            callBaseballGetStatus()
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value("END"))
        }

        @Test
        fun `베팅 포인트가 10포인트 이상, 1000포인트 이하면 게임이 시작된다`() {
            player.addPoint(1000)
            em.flush()
            em.clear()

            val result = callBaseballStart(1000)
                .andExpect(status().isOk)

            val baseball = gameFindService.findByMemberOrInit(player).baseball
            assertThat(baseball.baseballPerDay).isEqualTo(1) // 게임을 시작하면 play count가 증가한다.
            val data = redisUtil.getData(
                REDIS_KEY_PREFIX + player.id.toString() + "_" + baseball.baseballPerDay,
                BaseballResultEntity::class.java
            )
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
                    responseFields(
                        fieldWithPath("results").description("start API에선 빈 배열로 내려갑니다."),
                        fieldWithPath("earnablePoint").description("처음 할당된 획득할 포인트"),
                        fieldWithPath("bettingPoint").description("게임 시작 베팅 포인트"),
                        fieldWithPath("remainedSecond").description("이번 라운드 남은 초. 이 API에선 항상 ${SECOND_PER_GAME}으로 반환됩니다."),
                    )
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
                guessNumbers = listOf("1234"), correctNumber = "1234", bettingPoint = 1000,
                results = mutableListOf(
                    GuessResultEntity("1357", 0, 0),
                    GuessResultEntity("2468", 2, 2),
                    GuessResultEntity("7890", 3, 0)
                )
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.results").isArray)
                .andExpect(jsonPath("$.results[0]").exists())
                .andExpect(jsonPath("$.earnablePoint").isNumber)

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
                        fieldWithPath("earnablePoint").description("획득한 포인트 (마지막 게임이 아니면 0)"),
                        fieldWithPath("bettingPoint").description("게임 시작 베팅 포인트"),
                        fieldWithPath("remainedSecond").description("이번 라운드 남은 초. 이 API에선 항상 0으로 반환됩니다."),
                    ),
                )
            )
        }

        @Test
        fun `guessNumber가 4자가 아니면 guess는 실패한다`() {
            callBaseballGuess(guessNumbers = listOf("123"), correctNumber = "1234", bettingPoint = 1000)
                .andExpect(status().isBadRequest)
            callBaseballGuess(guessNumbers = listOf("12345"), correctNumber = "1234", bettingPoint = 1000)
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `맞췄으면 guess는 earnablePoint만큼 포인트를 부여해야 한다`() {
            val beforePlayerPoint = player.point
            val earnablePoint = 3000

            callBaseballGuess(
                guessNumbers = listOf("3456", "4567", "5678"),
                correctNumber = "5678",
                bettingPoint = 1000,
                earnablePoint = earnablePoint,
                results = mutableListOf(GuessResultEntity("1234", 2, 2), null, GuessResultEntity("2345", 3, 0))
            ).andExpect(status().isOk)

            assertThat(beforePlayerPoint + 2430).isEqualTo(player.point) // 초기 3000 포인트에서 2번 틀렸다고 가정
        }

        @Test
        fun `이미 맞췄으면 guess는 더이상 포인트 부여를 하지 않는다`() {
            val beforePlayerPoint = player.point

            callBaseballGuess(
                guessNumbers = listOf("1234"), correctNumber = "1234", bettingPoint = 1000,
                results = mutableListOf(GuessResultEntity("1234", 2, 2), null, GuessResultEntity("5678", 4, 0))
            ).andExpect(status().isOk)

            assertThat(beforePlayerPoint).isEqualTo(player.point)
        }

        @Test
        fun `시도 회수를 초과했을 경우 guess는 더이상 포인트 부여를 하지 않는다`() {
            val beforePlayerPoint = player.point

            callBaseballGuess(
                guessNumbers = listOf("1234"), correctNumber = "1234", bettingPoint = 1000,
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
                .andDo(
                    document(
                        "get-baseball-results",
                        requestCookies(
                            cookieWithName(JwtType.ACCESS_TOKEN.tokenName).description("ACCESS TOKEN"),
                            cookieWithName(JwtType.REFRESH_TOKEN.tokenName).description("REFRESH TOKEN")
                        ),
                        responseFields(
                            subsectionWithPath("results").description("타임아웃난 round는 null"),
                            fieldWithPath("earnablePoint").description("획득한 포인트 (오늘 끝낸 게임이 아니면 0)"),
                            fieldWithPath("bettingPoint").description("게임 시작 베팅 포인트"),
                            fieldWithPath("remainedSecond").description("이번 라운드 남은 초. ms 단위는 버림해서 내려갑니다."),
                        ),
                    )
                )
        }
    }
}
