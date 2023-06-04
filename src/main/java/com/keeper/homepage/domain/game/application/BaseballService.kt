package com.keeper.homepage.domain.game.application

import com.keeper.homepage.domain.game.dto.BaseballResult
import com.keeper.homepage.domain.game.dto.req.BaseballGuessResponse
import com.keeper.homepage.domain.member.entity.Member
import com.keeper.homepage.global.error.BusinessException
import com.keeper.homepage.global.error.ErrorCode
import com.keeper.homepage.global.util.redis.RedisUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.time.temporal.ChronoUnit

const val REDIS_KEY_PREFIX = "baseball_"
const val GUESS_NUMBER_LENGTH = 4
const val TRY_COUNT = 9

@Service
@Transactional(readOnly = true)
class BaseballService(
    val redisUtil: RedisUtil,
    val gameFindService: GameFindService
) {
    fun isAlreadyPlayed(requestMember: Member): Boolean {
        return gameFindService.findByMemberOrInit(requestMember)
            .baseball
            .isAlreadyPlayed
    }

    @Transactional
    fun start(requestMember: Member, bettingPoint: Int) {
        if (isAlreadyPlayed(requestMember)) {
            throw BusinessException(requestMember.id, "memberId", ErrorCode.IS_ALREADY_PLAYED)
        }
        if (bettingPoint <= 0) {
            throw BusinessException(requestMember.id, "memberId", ErrorCode.POINT_MUST_BE_POSITIVE)
        }
        if (requestMember.point < bettingPoint) {
            throw BusinessException(requestMember.id, "memberId", ErrorCode.NOT_ENOUGH_POINT)
        }

        val game = gameFindService.findByMemberOrInit(requestMember)
        requestMember.minusPoint(bettingPoint)
        game.baseball.increaseBaseballTimes()

        val baseballResult = BaseballResult(
            correctNumber = generateDistinctRandomNumber(GUESS_NUMBER_LENGTH),
            bettingPoint = bettingPoint
        )
        saveBaseballResultInRedis(requestMember.id, baseballResult)
    }

    private fun generateDistinctRandomNumber(length: Int): String {
        return (0..9).shuffled()
            .take(length)
            .joinToString(separator = "")
    }

    fun saveBaseballResultInRedis(requestMemberId: Long, baseballResult: BaseballResult) {
        redisUtil.setDataExpire(
            REDIS_KEY_PREFIX + requestMemberId.toString(),
            baseballResult,
            toMidNight()
        ) // 다음날 자정에 redis data expired
    }

    private fun toMidNight(): Long {
        return (LocalDateTime.now().plusDays(1)
            .truncatedTo(ChronoUnit.DAYS)
            .toEpochSecond(UTC) - LocalDateTime.now().toEpochSecond(UTC)) * 1000
    }

    @Transactional
    fun guess(requestMember: Member, guessNumber: String): BaseballGuessResponse {
        val baseballResult = redisUtil.getData(
            REDIS_KEY_PREFIX + requestMember.id.toString(),
            BaseballResult::class.java
        ).orElseThrow { throw BusinessException(requestMember.id, "memberId", ErrorCode.NOT_PLAYED_YET) }

        val gameEntity = gameFindService.findByMemberOrInit(requestMember)
        if (baseballResult.results.size >= TRY_COUNT || isAlreadyCorrect(baseballResult)) {
            return BaseballGuessResponse(baseballResult.results, gameEntity.baseball.baseballDayPoint)
        }

        val end = baseballResult.update(guessNumber)
        saveBaseballResultInRedis(requestMember.id, baseballResult)

        val earnedPoint = end.getEarnedPoint(baseballResult.bettingPoint)
        requestMember.addPoint(earnedPoint)
        gameEntity.baseball.baseballDayPoint = earnedPoint

        return BaseballGuessResponse(baseballResult.results, earnedPoint)
    }

    private fun isAlreadyCorrect(baseballResult: BaseballResult): Boolean {
        if (baseballResult.results.isEmpty()) {
            return false
        }
        if (baseballResult.results.last() == null) {
            return false
        }
        return baseballResult.results.last()!!.strike == GUESS_NUMBER_LENGTH
    }

    fun getResult(requestMember: Member): BaseballGuessResponse {
        if (!isAlreadyPlayed(requestMember)) {
            return BaseballGuessResponse.EMPTY
        }
        val baseballResult = redisUtil.getData(
            REDIS_KEY_PREFIX + requestMember.id.toString(),
            BaseballResult::class.java
        ).orElseThrow { throw BusinessException(requestMember.id, "memberId", ErrorCode.NOT_PLAYED_YET) }

        baseballResult.updateTimeoutGames()

        val gameEntity = gameFindService.findByMemberOrInit(requestMember)
        return BaseballGuessResponse(baseballResult.results, gameEntity.baseball.baseballDayPoint)
    }
}
