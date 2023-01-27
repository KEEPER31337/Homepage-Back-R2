package com.keeper.homepage.domain.member.entity.embedded;

import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.NOVEMBER;
import static java.time.Month.OCTOBER;
import static java.time.Month.SEPTEMBER;

import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

  public static final int MAX_LOGIN_ID_LENGTH = 80;
  public static final int MAX_EMAIL_LENGTH = 250;
  public static final int MAX_REAL_NAME_LENGTH = 40;
  public static final int MAX_NICKNAME_LENGTH = 40;
  public static final int MAX_STUDENT_ID_LENGTH = 45;
  private static final int KEEPER_FOUNDING_YEAR = 2009;
  private static final List<Month> SECOND_SEMESTER = List.of(SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER,
      JANUARY, FEBRUARY);

  @Column(name = "login_id", nullable = false, unique = true, length = MAX_LOGIN_ID_LENGTH)
  private String loginId;

  @Column(name = "email_address", nullable = false, unique = true, length = MAX_EMAIL_LENGTH)
  private String emailAddress;

  @Column(name = "password", nullable = false, length = 512)
  private String password;

  @Column(name = "real_name", nullable = false, length = MAX_REAL_NAME_LENGTH)
  private String realName;

  @Column(name = "nick_name", nullable = false, length = MAX_NICKNAME_LENGTH)
  private String nickname;

  @Column(name = "birthday")
  private LocalDate birthday;

  @Column(name = "student_id", unique = true, length = MAX_STUDENT_ID_LENGTH)
  private String studentId;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "thumbnail_id")
  private Thumbnail thumbnail;

  @Column(name = "generation")
  private Float generation;

  @Builder
  private Profile(String loginId, String emailAddress, String password, String realName,
      String nickname, LocalDate birthday, String studentId, Thumbnail thumbnail) {
    this.loginId = loginId;
    this.emailAddress = emailAddress;
    this.password = password;
    this.realName = realName;
    this.nickname = nickname;
    this.birthday = birthday;
    this.studentId = studentId;
    this.thumbnail = thumbnail;
    this.generation = generateGeneration(LocalDate.now());
  }

  private float generateGeneration(LocalDate now) {
    float generation = now.getYear() - KEEPER_FOUNDING_YEAR;
    if (SECOND_SEMESTER.contains(now.getMonth())) {
      generation += 0.5;
    }
    return generation;
  }
}
