package com.keeper.homepage.domain.game.api

import com.keeper.homepage.IntegrationTest
import com.keeper.homepage.domain.game.dto.req.BaseballStartRequest
import com.keeper.homepage.domain.member.entity.Member
import com.keeper.homepage.domain.member.entity.job.MemberJob
import jakarta.servlet.http.Cookie
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

    fun callBaseballIsAlreadyPlayed(
        accessCookies: Array<Cookie> = playerCookies
    ): ResultActions {
        return mockMvc.perform(
            get("$GAME_URL/baseball/is-already-played")
                .cookie(*accessCookies)
        )
    }

    fun callBaseballStart(
        bettingPoint: Long,
        accessCookies: Array<Cookie> = playerCookies
    ): ResultActions {
        return mockMvc.perform(
            post("$GAME_URL/baseball/start")
                .content(asJsonString(BaseballStartRequest(bettingPoint)))
                .contentType(APPLICATION_JSON)
                .cookie(*accessCookies)
        )
    }
}
