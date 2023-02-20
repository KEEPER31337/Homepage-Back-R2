package com.keeper.homepage.domain.game.entity;

import com.keeper.homepage.domain.game.entity.embedded.Dice;
import com.keeper.homepage.domain.game.entity.embedded.Lotto;
import com.keeper.homepage.domain.game.entity.embedded.Roulette;
import com.keeper.homepage.domain.member.entity.Member;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

  @Column(name = "last_play_time")
  private LocalDateTime lastPlayTime;

  @Embedded
  private Dice dice;

  @Embedded
  private Lotto lotto;

  @Embedded
  private Roulette roulette;

  public Game reset() {
    dice.resetDicePerDay();
    dice.resetDiceDayPoint();
    lotto.resetLottoPerDay();
    lotto.resetLottoDayPoint();
    roulette.resetRoulettePerDay();
    roulette.resetRouletteDayPoint();
    return this;
  }
}
