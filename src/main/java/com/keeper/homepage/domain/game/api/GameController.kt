package com.keeper.homepage.domain.game.api

import com.keeper.homepage.domain.game.application.BaseballService
import com.keeper.homepage.domain.game.application.GameService
import com.keeper.homepage.domain.game.dto.req.BaseballGuessRequest
import com.keeper.homepage.domain.game.dto.req.BaseballStartRequest
import com.keeper.homepage.domain.game.dto.res.*
import com.keeper.homepage.domain.game.entity.redis.SECOND_PER_GAME
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

    @GetMapping("/my-info")
    fun getMyInfo(@LoginMember requestMember: Member): ResponseEntity<GameInfoByMemberResponse> {
        return ResponseEntity.ok(gameService.getMyInfo(requestMember))
    }

    @GetMapping("/baseball/game-info")
    fun baseballGameInfoByMember(): ResponseEntity<BaseballInfoByMemberResponse> {
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
        return ResponseEntity.ok(BaseballResponse(emptyList(), earnablePoint, SECOND_PER_GAME))
    }

    @PostMapping("/baseball/guess")
    fun baseballGuess(
        @LoginMember requestMember: Member,
        @RequestBody @Valid request: BaseballGuessRequest
    ): ResponseEntity<BaseballResponse> {
        val (results, earnablePoint, remainedSeconds) = baseballService.guess(requestMember, request.guessNumber)
        return ResponseEntity.ok(BaseballResponse(results.map { i ->
            if (i == null) null else BaseballResponse.GuessResultResponse(i)
        }, earnablePoint, remainedSeconds))
    }

    @GetMapping("/baseball/result")
    fun getBaseballResult(
        @LoginMember requestMember: Member
    ): ResponseEntity<BaseballResponse> {
        val (results, earnablePoint, remainedSeconds) = baseballService.getResult(requestMember)
        return ResponseEntity.ok(BaseballResponse(results.map { i ->
            if (i == null) null else BaseballResponse.GuessResultResponse(i)
        }, earnablePoint, remainedSeconds))
    }
}
