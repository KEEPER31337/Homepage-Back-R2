package com.keeper.homepage.domain.game.api

import com.keeper.homepage.domain.game.application.BaseballService
import com.keeper.homepage.domain.game.application.GameService
import com.keeper.homepage.domain.game.dto.req.BaseballGuessRequest
import com.keeper.homepage.domain.game.dto.req.BaseballStartRequest
import com.keeper.homepage.domain.game.dto.res.BaseballResponse
import com.keeper.homepage.domain.game.dto.res.BaseballStatusResponse
import com.keeper.homepage.domain.game.dto.res.GameInfoByMemberResponse
import com.keeper.homepage.domain.game.dto.res.GameRankResponse
import com.keeper.homepage.domain.member.entity.Member
import com.keeper.homepage.global.config.security.annotation.LoginMember
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/game")
@RestController
class GameController(
    private val baseballService: BaseballService,
    private val gameService: GameService
) {
    @GetMapping("/rank")
    fun getGameRank(): ResponseEntity<List<GameRankResponse>> {
        return ResponseEntity.ok(gameService.getGameRanks())
    }

    @GetMapping("/baseball/game-info")
    fun baseballGameInfoByMember(): ResponseEntity<GameInfoByMemberResponse> {
        return ResponseEntity.ok(baseballService.getBaseballGameInfoByMember())
    }

    @GetMapping("/baseball/status")
    fun baseballGetStatus(@LoginMember requestMember: Member): ResponseEntity<BaseballStatusResponse> {
        val (baseballStatus, baseballPerDay) = baseballService.getStatus(requestMember)
        return ResponseEntity.ok(BaseballStatusResponse(baseballStatus, baseballPerDay))
    }

    @PostMapping("/baseball/start")
    fun baseballStart(
        @LoginMember requestMember: Member,
        @RequestBody @Valid request: BaseballStartRequest
    ): ResponseEntity<BaseballResponse> {
        val earnablePoint = baseballService.start(requestMember, request.bettingPoint)
        return ResponseEntity.ok(BaseballResponse(emptyList(), earnablePoint))
    }

    @PostMapping("/baseball/guess")
    fun baseballGuess(
        @LoginMember requestMember: Member,
        @RequestBody @Valid request: BaseballGuessRequest
    ): ResponseEntity<BaseballResponse> {
        val (results, earnablePoint) = baseballService.guess(requestMember, request.guessNumber)
        return ResponseEntity.ok(BaseballResponse(results.map { i ->
            if (i == null) null else BaseballResponse.GuessResultResponse(i)
        }, earnablePoint))
    }

    @GetMapping("/baseball/result")
    fun getBaseballResult(
        @LoginMember requestMember: Member
    ): ResponseEntity<BaseballResponse> {
        val (results, earnablePoint) = baseballService.getResult(requestMember)
        return ResponseEntity.ok(BaseballResponse(results.map { i ->
            if (i == null) null else BaseballResponse.GuessResultResponse(i)
        }, earnablePoint))
    }
}
