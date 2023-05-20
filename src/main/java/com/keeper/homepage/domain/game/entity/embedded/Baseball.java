package com.keeper.homepage.domain.game.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Baseball {

  private static final int BASEBALL_MAX_PLAYTIME = 1;

  @Column(name = "baseball_per_day", nullable = false)
  private Integer baseballPerDay;

  @Column(name = "baseball_day_point", nullable = false)
  private Integer baseballDayPoint;

  public static Baseball from(Integer baseballPerDay, Integer baseballDayPoint) {
    return new Baseball(baseballPerDay, baseballPerDay);
  }

  public boolean isAlreadyPlayed() {
    return baseballPerDay >= BASEBALL_MAX_PLAYTIME;
  }

  public void increaseBaseballTimes() {
    this.baseballPerDay += 1;
  }

  public void resetBaseballPerDay() {
    this.baseballPerDay = 0;
  }

  public void resetBaseballDayPoint() {
    this.baseballDayPoint = 0;
  }
}
