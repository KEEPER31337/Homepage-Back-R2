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
public class Lotto {

  private static final int LOTTO_MAX_PLAYTIME = 3;

  @Column(name = "lotto_per_day", nullable = false)
  private Integer lottoPerDay;

  @Column(name = "lotto_day_point", nullable = false)
  private Integer lottoDayPoint;

  public static Lotto from(Integer lottoPerDay, Integer lottoDayPoint) {
    return new Lotto(lottoPerDay, lottoDayPoint);
  }

  public void increaseLottoTimes() {
    this.lottoPerDay += 1;
  }

  public void resetLottoPerDay() {
    this.lottoPerDay = 0;
  }

  public void resetLottoDayPoint() {
    this.lottoDayPoint = 0;
  }
}
