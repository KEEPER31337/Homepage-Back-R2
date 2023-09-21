package com.keeper.homepage.domain.member.entity.embedded;

import static java.time.LocalDate.*;
import static java.util.UUID.*;

import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

  @Embedded
  private LoginId loginId;

  @Embedded
  private EmailAddress emailAddress;

  @Embedded
  private Password password;

  @Embedded
  private RealName realName;

  @Column(name = "birthday")
  private LocalDate birthday;

  @Embedded
  private StudentId studentId;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "thumbnail_id")
  private Thumbnail thumbnail;

  @Builder
  private Profile(LoginId loginId, EmailAddress emailAddress, Password password, RealName realName, LocalDate birthday,
      StudentId studentId, Thumbnail thumbnail) {
    this.loginId = loginId;
    this.emailAddress = emailAddress;
    this.password = password;
    this.realName = realName;
    this.birthday = birthday;
    this.studentId = studentId;
    this.thumbnail = thumbnail;
  }

  public void changePassword(String newPassword) {
    this.password = Password.from(newPassword);
  }

  public void update(Profile newProfile) {
    this.realName = newProfile.realName;
    this.studentId = newProfile.studentId;
    this.birthday = newProfile.birthday;
  }
  public void updateThumbnail(Thumbnail thumbnail) {
    this.thumbnail = thumbnail;
  }

  public void updateEmailAddress(String newEmailAddress) {
    this.emailAddress = EmailAddress.from(newEmailAddress);
  }

  public void deleteMemberProfile() {
    this.loginId = LoginId.from(generateRandomString(80));
    this.emailAddress = EmailAddress.from(generateRandomString(5) + '@' + generateRandomString(5) + ".com");
    this.studentId = StudentId.from(generateRandomDigitString(45));
    this.password = Password.from("delete");
    this.realName = RealName.from("탈퇴회원");
    this.birthday = null;
    this.thumbnail = null;
  }

  public static final Random RANDOM = new Random();

  private String generateRandomString(int length) {
    char leftLimit = '0';
    char rightLimit = 'z';

    return Profile.RANDOM.ints(leftLimit, rightLimit + 1)
        .filter(i -> Character.isAlphabetic(i) || Character.isDigit(i))
        .limit(length)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }

  private String generateRandomDigitString(int length) {
    final Random random = new Random();
    char leftLimit = '0';
    char rightLimit = '9';

    return random.ints(leftLimit, rightLimit + 1)
        .limit(length)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}
