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
public class Roulette {

  private static final int ROULETTE_MAX_PLAYTIME = 3;

  @Column(name = "roulette_per_day", nullable = false)
  private Integer roulettePerDay;

  @Column(name = "roulette_day_point", nullable = false)
  private Integer rouletteDayPoint;

  public static Roulette from(Integer roulettePerDay, Integer rouletteDayPoint) {
    return new Roulette(roulettePerDay, roulettePerDay);
  }

  public void increaseRouletteTimes() {
    this.roulettePerDay += 1;
  }

  public void resetRoulettePerDay() {
    this.roulettePerDay = 0;
  }

  public void resetRouletteDayPoint() {
    this.rouletteDayPoint = 0;
  }
}
