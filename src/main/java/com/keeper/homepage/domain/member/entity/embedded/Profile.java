package com.keeper.homepage.domain.member.entity.embedded;

import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
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
}
