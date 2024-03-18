package com.keeper.homepage.domain.game.application

import com.keeper.homepage.domain.game.dao.GameRepository
import com.keeper.homepage.domain.game.entity.Game
import com.keeper.homepage.domain.member.entity.Member
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class GameFindService(private val gameRepository: GameRepository) {

    fun findByMemberOrInit(member: Member): Game {
        return gameRepository.findByMemberAndPlayDate(member, LocalDate.now())
                .orElseGet { initWhenNotExistGameMemberInfo(member) }
    }

    fun findAllByPlayDate(playDate: LocalDate): List<Game> {
        return gameRepository.findAllByPlayDate(playDate)
    }

    private fun initWhenNotExistGameMemberInfo(member: Member) = gameRepository.save(Game.newInstance(member))
}
