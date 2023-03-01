package com.keeper.homepage.domain.game.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Dice {

  private static final int DICE_MAX_PLAYTIME = 3;

  @Column(name = "dice_per_day", nullable = false)
  private Integer dicePerDay;

  @Column(name = "dice_day_point", nullable = false)
  private Integer diceDayPoint;

  public static Dice from(Integer dicePerDay, Integer diceDayPoint) {
    return new Dice(dicePerDay, diceDayPoint);
  }

  public void increaseDiceTimes() {
    this.dicePerDay += 1;
  }

  public void resetDicePerDay() {
    this.dicePerDay = 0;
  }

  public void resetDiceDayPoint() {
    this.diceDayPoint = 0;
  }
}
