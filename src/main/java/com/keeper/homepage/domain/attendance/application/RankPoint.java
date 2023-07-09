package com.keeper.homepage.domain.attendance.application;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RankPoint {

  FIRST_RANK_POINT(1),
  SECOND_RANK_POINT(2),
  THIRD_RANK_POINT(3),
  ;

  private final int point;

  public static Optional<Integer> getRankPoint(int rank) {
    return Arrays.stream(RankPoint.values())
        .filter(rankPoint -> rankPoint.isMatch(rank))
        .map(RankPoint::getPoint)
        .findFirst();
  }

  private boolean isMatch(int rank) {
    return this.point == rank;
  }
}
