package com.keeper.homepage.domain.game.application

import com.keeper.homepage.domain.game.entity.redis.BaseballResultEntity
import com.keeper.homepage.domain.game.dto.res.BaseballGuessResponse
import com.keeper.homepage.domain.game.dto.res.GameInfoByMemberResponse
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
const val MAX_BETTING_POINT = 1000L
const val MIN_BETTING_POINT = 10L

@Service
@Transactional(readOnly = true)
class BaseballService(
    val redisUtil: RedisUtil,
    val gameFindService: GameFindService
) {
    fun getBaseballGameInfoByMember(): GameInfoByMemberResponse =
        GameInfoByMemberResponse(
            guessNumberLength = GUESS_NUMBER_LENGTH,
            tryCount = TRY_COUNT,
            maxBettingPoint = MAX_BETTING_POINT,
            minBettingPoint = MIN_BETTING_POINT,
        )

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

        val baseballResultEntity = BaseballResultEntity(
            correctNumber = generateDistinctRandomNumber(GUESS_NUMBER_LENGTH),
            bettingPoint = bettingPoint,
            earnablePoints = bettingPoint * 2 // TODO: 포인트 획득 전략 정해지면 다시 구현 (우선 처음엔 베팅포인트 * 2)
        )
        saveBaseballResultInRedis(requestMember.id, baseballResultEntity)
    }

    private fun generateDistinctRandomNumber(length: Int): String {
        return (0..9).shuffled()
            .take(length)
            .joinToString(separator = "")
    }

    fun saveBaseballResultInRedis(requestMemberId: Long, baseballResultEntity: BaseballResultEntity) {
        redisUtil.setDataExpire(
            REDIS_KEY_PREFIX + requestMemberId.toString(),
            baseballResultEntity,
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
        val baseballResultEntity = redisUtil.getData(
            REDIS_KEY_PREFIX + requestMember.id.toString(),
            BaseballResultEntity::class.java
        ).orElseThrow { throw BusinessException(requestMember.id, "memberId", ErrorCode.NOT_PLAYED_YET) }

        val gameEntity = gameFindService.findByMemberOrInit(requestMember)
        if (baseballResultEntity.results.size >= TRY_COUNT || isAlreadyCorrect(baseballResultEntity)) {
            return BaseballGuessResponse(baseballResultEntity.results, gameEntity.baseball.baseballDayPoint)
        }

        baseballResultEntity.update(guessNumber)
        saveBaseballResultInRedis(requestMember.id, baseballResultEntity)

        val earnablePoints = baseballResultEntity.earnablePoints
        requestMember.addPoint(earnablePoints)
        gameEntity.baseball.baseballDayPoint = earnablePoints

        return BaseballGuessResponse(baseballResultEntity.results, earnablePoints)
    }

    private fun isAlreadyCorrect(baseballResultEntity: BaseballResultEntity): Boolean {
        if (baseballResultEntity.results.isEmpty()) {
            return false
        }
        if (baseballResultEntity.results.last() == null) {
            return false
        }
        return baseballResultEntity.results.last()!!.strike == GUESS_NUMBER_LENGTH
    }

    fun getResult(requestMember: Member): BaseballGuessResponse {
        if (!isAlreadyPlayed(requestMember)) {
            return BaseballGuessResponse.EMPTY
        }
        val baseballResultEntity = redisUtil.getData(
            REDIS_KEY_PREFIX + requestMember.id.toString(),
            BaseballResultEntity::class.java
        ).orElseThrow { throw BusinessException(requestMember.id, "memberId", ErrorCode.NOT_PLAYED_YET) }

        baseballResultEntity.updateTimeoutGames()

        val gameEntity = gameFindService.findByMemberOrInit(requestMember)
        return BaseballGuessResponse(baseballResultEntity.results, gameEntity.baseball.baseballDayPoint)
    }
}
