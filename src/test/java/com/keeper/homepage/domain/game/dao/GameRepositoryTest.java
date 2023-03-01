package com.keeper.homepage.domain.game.dao;

import static org.assertj.core.api.Assertions.*;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.game.entity.Game;
import com.keeper.homepage.domain.game.entity.embedded.Dice;
import com.keeper.homepage.domain.game.entity.embedded.Lotto;
import com.keeper.homepage.domain.game.entity.embedded.Roulette;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.transaction.annotation.Transactional;

@Transactional
class GameRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("DB Default 테스트")
  class Default {

    @Test
    @DisplayName("Game 엔티티 저장")
    public void saveGame() throws Exception {
      // given
      Game game = gameTestHelper.builder()
          .dice(Dice.builder().dicePerDay(1).diceDayPoint(10000).build())
          .lotto(Lotto.builder().lottoPerDay(2).lottoDayPoint(20000).build())
          .roulette(Roulette.builder().roulettePerDay(3).rouletteDayPoint(30000).build()).build();

      // when
      Long savedId = gameRepository.save(game).getId();
      em.flush();
      em.clear();
      Optional<Game> findGame = gameRepository.findById(savedId);

      //then
      assertThat(findGame.get().getDice().getDicePerDay()).isEqualTo(1);
      assertThat(findGame.get().getLotto().getLottoPerDay()).isEqualTo(2);
      assertThat(findGame.get().getRoulette().getRoulettePerDay()).isEqualTo(3);
      assertThat(findGame.get().getDice().getDiceDayPoint()).isEqualTo(10000);
      assertThat(findGame.get().getLotto().getLottoDayPoint()).isEqualTo(20000);
      assertThat(findGame.get().getRoulette().getRouletteDayPoint()).isEqualTo(30000);
    }

    @Test
    @DisplayName("Game 엔티티 삭제")
    public void DeleteGame() throws Exception {
      // given
      Game game = gameTestHelper.generate();

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
      Game game = gameTestHelper.generate(); //build 될 때, lastPlayTime은 null이 들어감.

      //when
      LocalDateTime SavedTime = LocalDateTime.now().minusSeconds(1);
      Long savedId = gameRepository.save(game).getId();
      em.flush();
      em.clear();
      Optional<Game> findGame = gameRepository.findById(savedId);
      LocalDateTime findTime = LocalDateTime.now().plusSeconds(1);

      //then
      assertThat(findGame.get().getLastPlayTime()).isAfter(SavedTime);
      assertThat(findGame.get().getLastPlayTime()).isBefore(findTime);
    }
  }

  @Nested
  @DisplayName("Domain Method 테스트")
  class DomainMethodTest {

    @Test
    @DisplayName("주사위, 룰렛, 로또 게임 횟수 증가")
    public void increaseGameTimesTest() throws Exception {
      // given
      Game game = gameTestHelper.generate();

      // when
      Long savedId = gameRepository.save(game).getId();
      em.flush();
      em.clear();

      Optional<Game> findGame = gameRepository.findById(savedId);
      findGame.get().getDice().increaseDiceTimes();
      findGame.get().getLotto().increaseLottoTimes();
      findGame.get().getRoulette().increaseRouletteTimes();
      em.flush();
      em.clear();

      Optional<Game> updatedGame = gameRepository.findById(savedId);
      Integer dicePerDay = updatedGame.get().getDice().getDicePerDay();
      Integer lottoPerDay = updatedGame.get().getLotto().getLottoPerDay();
      Integer roulettePerDay = updatedGame.get().getRoulette().getRoulettePerDay();

      // then
      assertThat(dicePerDay).isEqualTo(1);
      assertThat(lottoPerDay).isEqualTo(1);
      assertThat(roulettePerDay).isEqualTo(1);
    }

    @Test
    @DisplayName("게임 필드 리셋")
    public void gameResetTest() throws Exception {
      // given
      Game game = gameTestHelper.builder()
          .dice(Dice.builder().dicePerDay(1).diceDayPoint(10000).build())
          .lotto(Lotto.builder().lottoPerDay(2).lottoDayPoint(20000).build())
          .roulette(Roulette.builder().roulettePerDay(3).rouletteDayPoint(30000).build()).build();

      // when
      Long savedId = gameRepository.save(game).getId();
      em.flush();
      em.clear();

      Optional<Game> findGame = gameRepository.findById(savedId);
      Game resetedGame = findGame.get().reset();

      // then
      assertThat(resetedGame.getLastPlayTime()).isNotNull();
      assertThat(resetedGame.getDice().getDicePerDay()).isEqualTo(0);
      assertThat(resetedGame.getLotto().getLottoPerDay()).isEqualTo(0);
      assertThat(resetedGame.getRoulette().getRoulettePerDay()).isEqualTo(0);
      assertThat(resetedGame.getDice().getDiceDayPoint()).isEqualTo(0);
      assertThat(resetedGame.getLotto().getLottoDayPoint()).isEqualTo(0);
      assertThat(resetedGame.getRoulette().getRouletteDayPoint()).isEqualTo(0);
    }
  }
}