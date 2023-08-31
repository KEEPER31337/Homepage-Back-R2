package com.keeper.homepage.domain.game.application

import com.keeper.homepage.domain.game.dto.res.BaseballInfoByMemberResponse
import com.keeper.homepage.domain.game.dto.res.BaseballResponse
import com.keeper.homepage.domain.game.dto.res.BaseballResponse.GuessResultResponse
import com.keeper.homepage.domain.game.dto.res.BaseballStatus
import com.keeper.homepage.domain.game.entity.Game
import com.keeper.homepage.domain.game.entity.embedded.Baseball.BASEBALL_MAX_PLAYTIME
import com.keeper.homepage.domain.game.entity.redis.BaseballResultEntity
import com.keeper.homepage.domain.game.entity.redis.BaseballResultEntity.GuessResultEntity
import com.keeper.homepage.domain.game.entity.redis.SECOND_PER_GAME
import com.keeper.homepage.domain.member.entity.Member
import com.keeper.homepage.global.error.BusinessException
import com.keeper.homepage.global.error.ErrorCode
import com.keeper.homepage.global.util.redis.RedisUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

const val REDIS_KEY_PREFIX = "baseball_"
const val GUESS_NUMBER_LENGTH = 4
const val TRY_COUNT = 9
const val MAX_BETTING_POINT = 1000L
const val MIN_BETTING_POINT = 10L
const val BETTING_POINT_MESSAGE = "야구 게임 베팅"
const val EARN_POINT_MESSAGE = "야구 게임 획득"

@Service
@Transactional(readOnly = true)
class BaseballService(
    val redisUtil: RedisUtil,
    val gameFindService: GameFindService
) {
    fun getBaseballGameInfoByMember(): BaseballInfoByMemberResponse =
        BaseballInfoByMemberResponse(
            guessNumberLength = GUESS_NUMBER_LENGTH,
            tryCount = TRY_COUNT,
            maxBettingPoint = MAX_BETTING_POINT,
            minBettingPoint = MIN_BETTING_POINT,
            maxPlayTime = BASEBALL_MAX_PLAYTIME,
        )

    @Transactional
    fun getStatus(requestMember: Member): Pair<BaseballStatus, Int> {
        val gameEntity = gameFindService.findByMemberOrInit(requestMember)
        if (gameEntity.baseball.isNeverStartedToday) {
            return Pair(BaseballStatus.NOT_START, gameEntity.baseball.baseballPerDay)
        }

        val baseballResult = getBaseballResultInRedis(requestMember, gameEntity)

        if (baseballResult.isEnd()) {
            return Pair(BaseballStatus.END, gameEntity.baseball.baseballPerDay)
        }

        baseballResult.updateTimeoutGames()
        saveBaseballResultInRedis(requestMember.id, baseballResult, gameEntity.baseball.baseballPerDay)

        return Pair(BaseballStatus.PLAYING, gameEntity.baseball.baseballPerDay)
    }

    @Transactional
    fun start(requestMember: Member, bettingPoint: Int): BaseballResponse {
        if (isAlreadyPlayedAllOfThem(requestMember)) {
            throw BusinessException(requestMember.id, "memberId", ErrorCode.IS_ALREADY_PLAYED)
        }
        if (bettingPoint <= 0) {
            throw BusinessException(requestMember.id, "memberId", ErrorCode.POINT_MUST_BE_POSITIVE)
        }
        if (requestMember.point < bettingPoint) {
            throw BusinessException(requestMember.id, "memberId", ErrorCode.NOT_ENOUGH_POINT)
        }

        val game = gameFindService.findByMemberOrInit(requestMember)
        requestMember.minusPoint(bettingPoint, BETTING_POINT_MESSAGE)
        game.baseball.increaseBaseballTimes()

        val baseballResultEntity = BaseballResultEntity(
            correctNumber = generateDistinctRandomNumber(GUESS_NUMBER_LENGTH),
            bettingPoint = bettingPoint,
            earnablePoint = bettingPoint * 2 // TODO: 포인트 획득 전략 정해지면 다시 구현 (우선 처음엔 베팅포인트 * 2)
        )
        saveBaseballResultInRedis(requestMember.id, baseballResultEntity, game.baseball.baseballPerDay)
        return BaseballResponse(emptyList(), bettingPoint, baseballResultEntity.earnablePoint, SECOND_PER_GAME)
    }

    private fun isAlreadyPlayedAllOfThem(member: Member): Boolean {
        return gameFindService.findByMemberOrInit(member)
            .baseball
            .isAlreadyPlayedAllOfThem
    }

    private fun generateDistinctRandomNumber(length: Int): String {
        return (0..9).shuffled()
            .take(length)
            .joinToString(separator = "")
    }

    fun saveBaseballResultInRedis(
        requestMemberId: Long,
        baseballResultEntity: BaseballResultEntity,
        baseballPerDay: Int
    ) {
        redisUtil.setDataExpire(
            REDIS_KEY_PREFIX + requestMemberId.toString() + "_" + baseballPerDay,
            baseballResultEntity,
            RedisUtil.toMidNight()
        ) // 다음날 자정에 redis data expired
    }

    @Transactional
    fun guess(
        requestMember: Member,
        guessNumber: String
    ): BaseballResponse {
        val gameEntity = gameFindService.findByMemberOrInit(requestMember)
        val baseballResultEntity = getBaseballResultInRedis(requestMember, gameEntity)

        if (baseballResultEntity.isEnd()) {
            return BaseballResponse(
                convertBaseballResult(baseballResultEntity.results),
                baseballResultEntity.bettingPoint,
                gameEntity.baseball.baseballDayPoint,
                0
            )
        }

        baseballResultEntity.update(guessNumber)
        saveBaseballResultInRedis(requestMember.id, baseballResultEntity, gameEntity.baseball.baseballPerDay)

        val earnablePoint = baseballResultEntity.earnablePoint

        if (baseballResultEntity.isEnd()) {
            requestMember.addPoint(earnablePoint, EARN_POINT_MESSAGE)
            gameEntity.baseball.baseballDayPoint = earnablePoint
        }

        return BaseballResponse(
            convertBaseballResult(baseballResultEntity.results),
            baseballResultEntity.bettingPoint,
            earnablePoint,
            0
        )
    }

    @Transactional
    fun getResult(requestMember: Member): BaseballResponse {
        if (isNotPlayedYet(requestMember)) {
            return BaseballResponse(emptyList(), 0, 0, 0)
        }
        val gameEntity = gameFindService.findByMemberOrInit(requestMember)
        val baseballResultEntity = getBaseballResultInRedis(requestMember, gameEntity)

        if (baseballResultEntity.isEnd()) {
            return BaseballResponse(
                convertBaseballResult(baseballResultEntity.results),
                baseballResultEntity.bettingPoint,
                baseballResultEntity.earnablePoint,
                0
            )
        }

        val remainedSeconds = baseballResultEntity.updateTimeoutGames()
        saveBaseballResultInRedis(requestMember.id, baseballResultEntity, gameEntity.baseball.baseballPerDay)

        return BaseballResponse(
            convertBaseballResult(baseballResultEntity.results),
            baseballResultEntity.bettingPoint,
            baseballResultEntity.earnablePoint,
            remainedSeconds
        )
    }

    private fun isNotPlayedYet(requestMember: Member): Boolean {
        return gameFindService.findByMemberOrInit(requestMember).baseball.baseballPerDay == 0
    }

    private fun getBaseballResultInRedis(requestMember: Member, gameEntity: Game): BaseballResultEntity {
        return redisUtil.getData(
            REDIS_KEY_PREFIX + requestMember.id.toString() + "_" + gameEntity.baseball.baseballPerDay,
            BaseballResultEntity::class.java
        ).orElseThrow { throw BusinessException(requestMember.id, "memberId", ErrorCode.NOT_PLAYED_YET) }
    }

    private fun convertBaseballResult(results: MutableList<GuessResultEntity?>): List<GuessResultResponse?> {
        return results.map { i ->
            if (i == null) null else GuessResultResponse(i)
        }
    }
}
