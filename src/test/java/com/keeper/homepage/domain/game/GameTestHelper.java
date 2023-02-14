package com.keeper.homepage.domain.game;

import com.keeper.homepage.domain.game.dao.GameRepository;
import com.keeper.homepage.domain.game.entity.Game;
import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameTestHelper {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    MemberTestHelper memberTestHelper;

    public Game generate() {
        return this.builder().build();
    }

    public GameBuilder builder() {
        return new GameBuilder();
    }

    public final class GameBuilder {

        private Member member;
        private LocalDateTime lastPlayTime;
        private Integer dicePerDay;
        private Integer roulettePerDay;
        private Integer lottoPerDay;
        private Integer diceDayPoint;
        private Integer rouletteDayPoint;
        private Integer lottoDayPoint;

        private GameBuilder() {
        }

        public GameBuilder member(Member member) {
            this.member = member;
            return this;
        }

        public GameBuilder lastPlayTime(LocalDateTime lastPlayTime) {
            this.lastPlayTime = lastPlayTime;
            return this;
        }

        public GameBuilder dicePerDay(Integer dicePerDay) {
            this.dicePerDay = dicePerDay;
            return this;
        }

        public GameBuilder roulettePerDay(Integer roulettePerDay) {
            this.roulettePerDay = roulettePerDay;
            return this;
        }

        public GameBuilder lottoPerDay(Integer lottoPerDay) {
            this.lottoPerDay = lottoPerDay;
            return this;
        }

        public GameBuilder diceDayPoint(Integer diceDayPoint) {
            this.diceDayPoint = diceDayPoint;
            return this;
        }

        public GameBuilder rouletteDayPoint(Integer rouletteDayPoint) {
            this.rouletteDayPoint = rouletteDayPoint;
            return this;
        }

        public GameBuilder lottoDayPoint(Integer lottoDayPoint) {
            this.lottoDayPoint = lottoDayPoint;
            return this;
        }

        public Game build() {
            return gameRepository.save(Game.builder()
                .member(member != null ? member : memberTestHelper.generate())
                .lastPlayTime(null)
                .dicePerDay(dicePerDay != null ? dicePerDay : 0)
                .lottoPerDay(lottoPerDay != null ? lottoPerDay : 0)
                .roulettePerDay(roulettePerDay != null ? roulettePerDay : 0)
                .diceDayPoint(diceDayPoint != null ? diceDayPoint : 0)
                .lottoDayPoint(lottoDayPoint != null ? lottoDayPoint : 0)
                .rouletteDayPoint(rouletteDayPoint != null ? rouletteDayPoint : 0)
                .build());
        }
    }
}
