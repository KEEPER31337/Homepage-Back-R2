package com.keeper.homepage.global.util.semester;

import static java.time.Month.APRIL;
import static java.time.Month.AUGUST;
import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.JULY;
import static java.time.Month.JUNE;
import static java.time.Month.MARCH;
import static java.time.Month.MAY;
import static java.time.Month.NOVEMBER;
import static java.time.Month.OCTOBER;
import static java.time.Month.SEPTEMBER;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class SemesterUtil {

  private static final List<Month> FIRST_SEMESTER = List.of(MARCH, APRIL, MAY, JUNE, JULY, AUGUST);
  private static final List<Month> SECOND_SEMESTER = List.of(SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER,
      JANUARY, FEBRUARY);

  public static LocalDate getSemesterFirstDate(LocalDate now) {
    if (FIRST_SEMESTER.contains(now.getMonth())) {
      return LocalDate.of(now.getYear(), MARCH, 1);
    }
    if (now.getMonth() == JANUARY || now.getMonth() == FEBRUARY) {
      return LocalDate.of(now.getYear() - 1, SEPTEMBER, 1);
    }
    return LocalDate.of(now.getYear(), SEPTEMBER, 1);
  }
}
