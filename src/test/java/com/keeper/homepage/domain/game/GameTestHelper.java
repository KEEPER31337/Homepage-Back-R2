package com.keeper.homepage.domain.game;

import com.keeper.homepage.domain.game.dao.GameRepository;
import com.keeper.homepage.domain.game.entity.Game;
import com.keeper.homepage.domain.game.entity.embedded.Dice;
import com.keeper.homepage.domain.game.entity.embedded.Lotto;
import com.keeper.homepage.domain.game.entity.embedded.Roulette;
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
    private Dice dice;
    private Lotto lotto;
    private Roulette roulette;

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

    public GameBuilder dice(Dice dice) {
      this.dice = dice;
      return this;
  }

    public GameBuilder lotto(Lotto lotto) {
      this.lotto = lotto;
      return this;
    }

    public GameBuilder roulette(Roulette roulette) {
      this.roulette = roulette;
      return this;
    }

    public Game build() {
      return gameRepository.save(
          Game.builder().member(member != null ? member : memberTestHelper.generate())
              .lastPlayTime(null)
              .dice(dice != null ? dice : Dice.from(0, 0))
              .lotto(lotto != null ? lotto : Lotto.from(0, 0))
              .roulette(roulette != null ? roulette : Roulette.from(0, 0)).build());
    }
  }
}
