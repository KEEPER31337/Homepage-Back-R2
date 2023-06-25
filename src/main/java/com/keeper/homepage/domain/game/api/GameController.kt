package com.keeper.homepage.domain.game.api

import com.keeper.homepage.domain.game.application.BaseballService
import com.keeper.homepage.domain.game.dto.req.BaseballGuessRequest
import com.keeper.homepage.domain.game.dto.req.BaseballStartRequest
import com.keeper.homepage.domain.game.dto.res.BaseballGuessResponse
import com.keeper.homepage.domain.game.dto.res.GameInfoByMemberResponse
import com.keeper.homepage.domain.member.entity.Member
import com.keeper.homepage.global.config.security.annotation.LoginMember
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/game")
@RestController
class GameController(
    private val baseballService: BaseballService
) {
    @GetMapping("/baseball/game-info")
    fun baseballGameInfoByMember(): ResponseEntity<GameInfoByMemberResponse> {
        return ResponseEntity.ok(baseballService.getBaseballGameInfoByMember())
    }

    @GetMapping("/baseball/is-already-played")
    fun baseballIsAlreadyPlayed(@LoginMember requestMember: Member): ResponseEntity<Boolean> {
        return ResponseEntity.ok(baseballService.isAlreadyPlayed(requestMember))
    }

    @PostMapping("/baseball/start")
    fun baseballStart(
        @LoginMember requestMember: Member,
        @RequestBody @Valid request: BaseballStartRequest
    ): ResponseEntity<Void> {
        baseballService.start(requestMember, request.bettingPoint)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/baseball/guess")
    fun baseballGuess(
        @LoginMember requestMember: Member,
        @RequestBody @Valid request: BaseballGuessRequest
    ): ResponseEntity<BaseballGuessResponse> {
        val (results, earnablePoints) = baseballService.guess(requestMember, request.guessNumber)
        return ResponseEntity.ok(BaseballGuessResponse(results.map { i ->
            if (i == null) null else BaseballGuessResponse.GuessResultResponse(i)
        }, earnablePoints))
    }

    @GetMapping("/baseball/result")
    fun getBaseballResult(
        @LoginMember requestMember: Member
    ): ResponseEntity<BaseballGuessResponse> {
        val (results, earnablePoints) = baseballService.getResult(requestMember)
        return ResponseEntity.ok(BaseballGuessResponse(results.map { i ->
            if (i == null) null else BaseballGuessResponse.GuessResultResponse(i)
        }, earnablePoints))
    }
}
