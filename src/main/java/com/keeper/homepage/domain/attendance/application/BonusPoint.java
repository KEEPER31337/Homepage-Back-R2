package com.keeper.homepage.domain.attendance.application;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BonusPoint {

  YEAR_BONUS_POINT(365, 100000),
  MONTH_BONUS_POINT(30, 10000),
  WEEK_BONUS_POINT(7, 3000),
  ;

  private final int date;
  private final int point;

  public static Optional<Integer> getBonusPoint(int date) {
    return Arrays.stream(BonusPoint.values())
        .filter(bonusPoint -> bonusPoint.isMatch(date))
        .map(BonusPoint::getPoint)
        .findFirst();
  }

  private boolean isMatch(int date) {
    return this.date == date;
  }
}
