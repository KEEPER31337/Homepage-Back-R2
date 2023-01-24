package com.keeper.homepage.domain.member.entity;

import com.keeper.homepage.domain.member.entity.embedded.MeritDemerit;
import com.keeper.homepage.domain.member.entity.embedded.Profile;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Embedded
  private Profile profile;

  @Column(name = "point", nullable = false)
  private Integer point;

  @Column(name = "level", nullable = false)
  private Integer level;

  @Embedded
  private MeritDemerit meritDemerit;

  @Column(name = "total_attendance", nullable = false)
  private Integer totalAttendance;

  @Builder
  private Member(String loginId, String emailAddress, String password, String realName,
      String nickname, LocalDate birthday, String studentId, Integer point, Integer level,
      Thumbnail thumbnail, Integer merit, Integer demerit, Float generation,
      Integer totalAttendance) {
    this.profile = Profile.builder()
        .loginId(loginId)
        .emailAddress(emailAddress)
        .password(password)
        .realName(realName)
        .nickname(nickname)
        .birthday(birthday)
        .studentId(studentId)
        .thumbnail(thumbnail)
        .generation(generation)
        .build();
    this.point = point;
    this.level = level;
    this.meritDemerit = MeritDemerit.builder()
        .merit(merit == null ? 0 : merit)
        .demerit(demerit == null ? 0 : demerit)
        .build();
    this.totalAttendance = totalAttendance;
  }
}
