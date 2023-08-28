package com.keeper.homepage.domain.game.api

import com.keeper.homepage.IntegrationTest
import com.keeper.homepage.domain.game.dto.req.BaseballGuessRequest
import com.keeper.homepage.domain.game.dto.req.BaseballStartRequest
import com.keeper.homepage.domain.game.entity.redis.BaseballResultEntity
import com.keeper.homepage.domain.member.entity.Member
import com.keeper.homepage.domain.member.entity.job.MemberJob
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.test.web.servlet.ResultActions

const val GAME_URL = "/game"

class GameApiTestHelper : IntegrationTest() {

    lateinit var player: Member
    lateinit var playerCookies: Array<Cookie>

    @BeforeEach
    fun setUp() {
        player = memberTestHelper.generate()
        player.assignJob(MemberJob.MemberJobType.ROLE_회원)
        playerCookies = memberTestHelper.getTokenCookies(player)
    }

    @AfterEach
    fun flushAll() {
        redisUtil.flushAll()
    }

    fun callGetGameRank(
        accessCookies: Array<Cookie> = playerCookies
    ): ResultActions {
        return mockMvc.perform(
            get("$GAME_URL/rank")
                .cookie(*accessCookies)
        )
    }

    fun callGetMyGameInfo(
        accessCookies: Array<Cookie> = playerCookies
    ): ResultActions {
        return mockMvc.perform(
            get("$GAME_URL/my-info")
                .cookie(*accessCookies)
        )
    }

    fun callBaseballGameInfo(
        accessCookies: Array<Cookie> = playerCookies
    ): ResultActions {
        return mockMvc.perform(
            get("$GAME_URL/baseball/game-info")
                .cookie(*accessCookies)
        )
    }

    fun callBaseballGetStatus(
        accessCookies: Array<Cookie> = playerCookies
    ): ResultActions {
        return mockMvc.perform(
            get("$GAME_URL/baseball/status")
                .cookie(*accessCookies)
        )
    }

    fun callBaseballStart(
        bettingPoint: Int,
        accessCookies: Array<Cookie> = playerCookies
    ): ResultActions {
        return mockMvc.perform(
            post("$GAME_URL/baseball/start")
                .content(asJsonString(BaseballStartRequest(bettingPoint)))
                .contentType(APPLICATION_JSON)
                .cookie(*accessCookies)
        )
    }

    fun callBaseballGuess(
        guessNumbers: List<String>,
        correctNumber: String,
        bettingPoint: Int,
        results: MutableList<BaseballResultEntity.GuessResultEntity?> = mutableListOf(),
        earnablePoint: Int = 1000,
        accessCookies: Array<Cookie> = playerCookies
    ): ResultActions {
        baseballService.saveBaseballResultInRedis(
            player.id,
            BaseballResultEntity(correctNumber, bettingPoint, results, earnablePoint),
            0,
        )
        for (guessNumber in guessNumbers.subList(0, guessNumbers.size - 1)) {
            callBaseballGuess(guessNumber, accessCookies)
        }
        return callBaseballGuess(guessNumbers.last(), accessCookies)
    }

    private fun callBaseballGuess(guessNumber: String, accessCookies: Array<Cookie>): ResultActions {
        return mockMvc.perform(
            post("$GAME_URL/baseball/guess")
                .content(asJsonString(BaseballGuessRequest(guessNumber)))
                .contentType(APPLICATION_JSON)
                .cookie(*accessCookies))
    }

    fun callGetBaseballResult(
        results: MutableList<BaseballResultEntity.GuessResultEntity?> = mutableListOf(),
        earnablePoint: Int = 1000,
        accessCookies: Array<Cookie> = playerCookies
    ): ResultActions {
        baseballService.saveBaseballResultInRedis(
            player.id,
            BaseballResultEntity("1234", 1000, results, earnablePoint),
            1,
        )
        return mockMvc.perform(
            get("$GAME_URL/baseball/result")
                .cookie(*accessCookies)
        )
    }

    fun gameStart() {
        gameFindService.findByMemberOrInit(player)
            .baseball
            .increaseBaseballTimes()
    }
}
