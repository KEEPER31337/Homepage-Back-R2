package com.keeper.homepage.domain.member.entity.embedded;

import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.NOVEMBER;
import static java.time.Month.OCTOBER;
import static java.time.Month.SEPTEMBER;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Generation {

  private static final int KEEPER_FOUNDING_YEAR = 2009;
  private static final List<Month> SECOND_SEMESTER = List.of(SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER,
      JANUARY, FEBRUARY);

  @Column(name = "generation")
  private Float generation;

  public static Generation generateGeneration(LocalDate now) {
    float generation = now.getYear() - KEEPER_FOUNDING_YEAR;
    if (SECOND_SEMESTER.contains(now.getMonth())) {
      generation += 0.5;
    }
    return new Generation(generation);
  }
}
