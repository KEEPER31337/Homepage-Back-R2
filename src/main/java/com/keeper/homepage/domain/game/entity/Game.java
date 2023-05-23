package com.keeper.homepage.domain.game.entity;

import com.keeper.homepage.domain.game.entity.embedded.Baseball;
import com.keeper.homepage.domain.game.entity.embedded.Dice;
import com.keeper.homepage.domain.game.entity.embedded.Lotto;
import com.keeper.homepage.domain.game.entity.embedded.Roulette;
import com.keeper.homepage.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "game_member_info")
@Entity
public class Game {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Column(name = "play_date", nullable = false)
  private LocalDate playDate;

  @Column(name = "last_play_time")
  private LocalDateTime lastPlayTime;

  @Embedded
  private Dice dice;

  @Embedded
  private Lotto lotto;

  @Embedded
  private Roulette roulette;

  @Embedded
  private Baseball baseball;

  public Game(Long id, Member member, LocalDate playDate, LocalDateTime lastPlayTime, Dice dice, Lotto lotto, Roulette roulette,
      Baseball baseball) {
    this.id = id;
    this.member = member;
    this.playDate = playDate;
    this.lastPlayTime = lastPlayTime;
    this.dice = dice;
    this.lotto = lotto;
    this.roulette = roulette;
    this.baseball = baseball;
  }

  public static Game newInstance(Member member) {
    return new Game(null, member, LocalDate.now(), null,
        Dice.from(0, 0), Lotto.from(0, 0),
        Roulette.from(0, 0), Baseball.from(0, 0));
  }

  public Game reset() {
    dice.resetDicePerDay();
    dice.resetDiceDayPoint();
    lotto.resetLottoPerDay();
    lotto.resetLottoDayPoint();
    roulette.resetRoulettePerDay();
    roulette.resetRouletteDayPoint();
    baseball.resetBaseballPerDay();
    baseball.resetBaseballDayPoint();
    return this;
  }

  public Long getId() {
    return id;
  }

  public Member getMember() {
    return member;
  }

  public LocalDateTime getLastPlayTime() {
    return lastPlayTime;
  }

  public Dice getDice() {
    return dice;
  }

  public Lotto getLotto() {
    return lotto;
  }

  public Roulette getRoulette() {
    return roulette;
  }

  public Baseball getBaseball() {
    return baseball;
  }
}
