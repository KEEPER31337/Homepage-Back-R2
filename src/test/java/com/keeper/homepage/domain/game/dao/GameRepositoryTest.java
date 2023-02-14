package com.keeper.homepage.domain.game.dao;

import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.*;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.game.entity.Game;
import com.keeper.homepage.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.transaction.annotation.Transactional;

@Transactional
class GameRepositoryTest extends IntegrationTest {

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberTestHelper.generate();
    }

    @Nested
    @DisplayName("DB Default 테스트")
    class Default {

        @Test
        @DisplayName("Game 엔티티 저장")
        public void checkDefault() throws Exception {
            // given
            Game game = Game.builder()
                .member(member)
                .dicePerDay(0)
                .lottoPerDay(0)
                .roulettePerDay(0)
                .diceDayPoint(10000)
                .lottoDayPoint(20000)
                .rouletteDayPoint(30000)
                .build();

            // when
            Long savedId = gameRepository.save(game).getId();
            em.flush();
            em.clear();
            Optional<Game> findGame = gameRepository.findById(savedId);

            //then
            assertThat(findGame.get().getDicePerDay()).isEqualTo(0);
            assertThat(findGame.get().getLottoPerDay()).isEqualTo(0);
            assertThat(findGame.get().getRoulettePerDay()).isEqualTo(0);

            assertThat(findGame.get().getDiceDayPoint()).isEqualTo(10000);
            assertThat(findGame.get().getLottoDayPoint()).isEqualTo(20000);
            assertThat(findGame.get().getRouletteDayPoint()).isEqualTo(30000);
        }

        @Test
        @DisplayName("Game 엔티티 삭제")
        public void DeleteGame() throws Exception {
            // given
            Game game = Game.builder()
                .member(member)
                .dicePerDay(0)
                .lottoPerDay(0)
                .roulettePerDay(0)
                .diceDayPoint(10000)
                .lottoDayPoint(20000)
                .rouletteDayPoint(30000)
                .build();

            // when
            Long savedId = gameRepository.save(game).getId();
            em.flush();
            em.clear();
            gameRepository.deleteById(savedId);
            Optional<Game> findGame = gameRepository.findById(savedId);

            // then
            assertThat(findGame.isPresent()).isFalse();
        }

        @Test
        @DisplayName("마지막 플레이 시간은 DB에서 저장된다.")
        public void timeTest() throws Exception {
            // given
            Game game = Game.builder()
                .member(member)
                .dicePerDay(0)
                .lottoPerDay(0)
                .roulettePerDay(0)
                .diceDayPoint(10000)
                .lottoDayPoint(20000)
                .rouletteDayPoint(30000)
                .build();

            //when
            LocalDateTime beforeSaveTime = now();
            Long savedId = gameRepository.save(game).getId();
            em.flush();
            em.clear();
            Optional<Game> findGame = gameRepository.findById(savedId);

            // then
            assertThat(findGame.get().getLastPlayTime()).isBefore(beforeSaveTime);
        }


    }

    @Nested
    @DisplayName("Domain Method 테스트")
    class DomainMethodTest {

        @Test
        @DisplayName("주사위, 룰렛, 로또 게임 횟수 증가")
        public void increaseGameTimesTest() throws Exception {
            // given
            Game game = Game.builder()
                .member(member)
                .dicePerDay(0)
                .lottoPerDay(0)
                .roulettePerDay(0)
                .diceDayPoint(10000)
                .lottoDayPoint(20000)
                .rouletteDayPoint(30000)
                .build();

            // when
            Long savedId = gameRepository.save(game).getId();
            em.flush();
            em.clear();

            Optional<Game> findGame = gameRepository.findById(savedId);
            findGame.get().increaseDiceTimes();
            findGame.get().increaseLottoTimes();
            findGame.get().increaseRouletteTimes();
            em.flush();
            em.clear();

            Optional<Game> updatedGame = gameRepository.findById(savedId);
            Integer dicePerDay = updatedGame.get().getDicePerDay();
            Integer lottoPerDay = updatedGame.get().getLottoPerDay();
            Integer roulettePerDay = updatedGame.get().getRoulettePerDay();

            // then
            assertThat(dicePerDay).isEqualTo(1);
            assertThat(lottoPerDay).isEqualTo(1);
            assertThat(roulettePerDay).isEqualTo(1);
        }

        @Test
        @DisplayName("게임 필드 리셋")
        public void gameResetTest() throws Exception {
            // given
            Game game = Game.builder()
                .member(member)
                .dicePerDay(0)
                .lottoPerDay(0)
                .roulettePerDay(0)
                .diceDayPoint(10000)
                .lottoDayPoint(20000)
                .rouletteDayPoint(30000)
                .build();

            // when
            Long savedId = gameRepository.save(game).getId();
            em.flush();
            em.clear();

            Optional<Game> findGame = gameRepository.findById(savedId);
            Game resetedGame = findGame.get().reset();

            // then
            assertThat(resetedGame.getLastPlayTime()).isNotNull();
            assertThat(resetedGame.getDicePerDay()).isEqualTo(0);
            assertThat(resetedGame.getLottoPerDay()).isEqualTo(0);
            assertThat(resetedGame.getRoulettePerDay()).isEqualTo(0);

            assertThat(resetedGame.getDiceDayPoint()).isEqualTo(0);
            assertThat(resetedGame.getLottoDayPoint()).isEqualTo(0);
            assertThat(resetedGame.getRouletteDayPoint()).isEqualTo(0);
        }
    }
}