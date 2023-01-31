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

  public static final int MAX_NICKNAME_LENGTH = 40;
  public static final int MAX_STUDENT_ID_LENGTH = 45;

  @Embedded
  private LoginId loginId;

  @Embedded
  private EmailAddress emailAddress;

  @Embedded
  private Password password;

  @Embedded
  private RealName realName;

  @Column(name = "nick_name", nullable = false, length = MAX_NICKNAME_LENGTH)
  private String nickname;

  @Column(name = "birthday")
  private LocalDate birthday;

  @Column(name = "student_id", unique = true, length = MAX_STUDENT_ID_LENGTH)
  private String studentId;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "thumbnail_id")
  private Thumbnail thumbnail;

  @Builder
  private Profile(LoginId loginId, EmailAddress emailAddress, Password password, RealName realName,
      String nickname, LocalDate birthday, String studentId, Thumbnail thumbnail) {
    this.loginId = loginId;
    this.emailAddress = emailAddress;
    this.password = password;
    this.realName = realName;
    this.nickname = nickname;
    this.birthday = birthday;
    this.studentId = studentId;
    this.thumbnail = thumbnail;
  }
}
