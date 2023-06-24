package com.keeper.homepage.domain.game.api

import com.keeper.homepage.IntegrationTest
import com.keeper.homepage.domain.game.dto.BaseballResult
import com.keeper.homepage.domain.game.dto.req.BaseballGuessRequest
import com.keeper.homepage.domain.game.dto.req.BaseballStartRequest
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

    fun callBaseballGameInfo(
        accessCookies: Array<Cookie> = playerCookies
    ): ResultActions {
        return mockMvc.perform(
            get("$GAME_URL/baseball/game-info")
                .cookie(*accessCookies)
        )
    }

    fun callBaseballIsAlreadyPlayed(
        accessCookies: Array<Cookie> = playerCookies
    ): ResultActions {
        return mockMvc.perform(
            get("$GAME_URL/baseball/is-already-played")
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
        guessNumber: String,
        correctNumber: String,
        bettingPoint: Int,
        results: MutableList<BaseballResult.GuessResult?> = mutableListOf(),
        accessCookies: Array<Cookie> = playerCookies
    ): ResultActions {
        baseballService.saveBaseballResultInRedis(player.id, BaseballResult(correctNumber, bettingPoint, results))
        return mockMvc.perform(
            post("$GAME_URL/baseball/guess")
                .content(asJsonString(BaseballGuessRequest(guessNumber)))
                .contentType(APPLICATION_JSON)
                .cookie(*accessCookies)
        )
    }

    fun callGetBaseballResult(
        results: MutableList<BaseballResult.GuessResult?> = mutableListOf(),
        accessCookies: Array<Cookie> = playerCookies
    ): ResultActions {
        baseballService.saveBaseballResultInRedis(player.id, BaseballResult("1234", 1000, results))
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
