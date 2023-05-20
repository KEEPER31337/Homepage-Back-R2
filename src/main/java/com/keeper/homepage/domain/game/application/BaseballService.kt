package com.keeper.homepage.domain.game.application

import com.keeper.homepage.domain.game.dao.GameRepository
import com.keeper.homepage.domain.game.dto.BaseballResult
import com.keeper.homepage.domain.game.entity.Game
import com.keeper.homepage.domain.member.entity.Member
import com.keeper.homepage.global.error.BusinessException
import com.keeper.homepage.global.error.ErrorCode
import com.keeper.homepage.global.util.redis.RedisUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

const val REDIS_KEY_PREFIX = "baseball_"

@Service
@Transactional(readOnly = true)
class BaseballService(
    val redisUtil: RedisUtil,
    val gameRepository: GameRepository
) {
    fun isAlreadyPlayed(requestMember: Member): Boolean {
        initWhenNotExistGameMemberInfo(requestMember)
        return gameRepository.findAllByMember(requestMember)
            .map { game -> game.baseball.isAlreadyPlayed }
            .orElse(true)
    }

    private fun initWhenNotExistGameMemberInfo(requestMember: Member) {
        if (gameRepository.findAllByMember(requestMember).isEmpty) {
            gameRepository.save(Game.newInstance(requestMember))
        }
    }

    @Transactional
    fun start(requestMember: Member, bettingPoint: Long) {
        if (isAlreadyPlayed(requestMember)) {
            throw BusinessException(requestMember.id, "memberId", ErrorCode.IS_ALREADY_PLAYED)
        }
        if (bettingPoint <= 0) {
            throw BusinessException(requestMember.id, "memberId", ErrorCode.POINT_MUST_BE_POSITIVE)
        }
        if (requestMember.point < bettingPoint) {
            throw BusinessException(requestMember.id, "memberId", ErrorCode.NOT_ENOUGH_POINT)
        }

        val game = gameRepository.findAllByMember(requestMember).get()
        requestMember.minusPoint(bettingPoint.toInt())
        game.baseball.increaseBaseballTimes()

        val baseballResult = BaseballResult(bettingPoint = bettingPoint)
        redisUtil.setDataExpire(
            REDIS_KEY_PREFIX + requestMember.id.toString(),
            baseballResult,
            toMidNight()
        ) // 다음날 자정에 redis data expired
    }

    private fun toMidNight(): Long {
        return (LocalDateTime.now().plusDays(1)
            .truncatedTo(ChronoUnit.DAYS)
            .toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) * 1000
    }
}
