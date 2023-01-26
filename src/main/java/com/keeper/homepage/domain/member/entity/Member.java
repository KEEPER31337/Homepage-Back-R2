package com.keeper.homepage.domain.member.entity;

import com.keeper.homepage.domain.attendance.entity.Attendance;
import com.keeper.homepage.domain.library.entity.BookBorrowInfo;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

  public static final int MAX_LOGIN_ID_LENGTH = 80;
  public static final int MAX_EMAIL_LENGTH = 250;
  public static final int MAX_REALNAME_LENGTH = 40;
  public static final int MAX_NICKNAME_LENGTH = 40;
  public static final int MAX_STUDENT_ID_LENGTH = 45;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "login_id", nullable = false, unique = true, length = MAX_LOGIN_ID_LENGTH)
  private String loginId;

  @Column(name = "email_address", nullable = false, unique = true, length = MAX_EMAIL_LENGTH)
  private String emailAddress;

  @Column(name = "password", nullable = false, length = 512)
  private String password;

  @Column(name = "real_name", nullable = false, length = MAX_REALNAME_LENGTH)
  private String realName;

  @Column(name = "nick_name", nullable = false, length = MAX_NICKNAME_LENGTH)
  private String nickname;

  @Column(name = "birthday")
  private LocalDate birthday;

  @Column(name = "student_id", unique = true, length = MAX_STUDENT_ID_LENGTH)
  private String studentId;

  @Column(name = "point", nullable = false)
  private Integer point;

  @Column(name = "level", nullable = false)
  private Integer level;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "thumbnail_id")
  private Thumbnail thumbnail;

  @Column(name = "merit", nullable = false)
  private Integer merit;

  @Column(name = "demerit", nullable = false)
  private Integer demerit;

  @Column(name = "generation")
  private Float generation;

  @Column(name = "total_attendance", nullable = false)
  private Integer totalAttendance;

  @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
  private final List<Attendance> memberAttendance = new ArrayList<>();

  @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
  private final List<BookBorrowInfo> bookBorrowInfos = new ArrayList<>();

  @Builder
  private Member(String loginId, String emailAddress, String password, String realName,
      String nickname, LocalDate birthday, String studentId, Integer point, Integer level,
      Thumbnail thumbnail, Integer merit, Integer demerit, Float generation,
      Integer totalAttendance) {
    this.loginId = loginId;
    this.emailAddress = emailAddress;
    this.password = password;
    this.realName = realName;
    this.nickname = nickname;
    this.birthday = birthday;
    this.studentId = studentId;
    this.point = point;
    this.level = level;
    this.thumbnail = thumbnail;
    this.merit = merit;
    this.demerit = demerit;
    this.generation = generation;
    this.totalAttendance = totalAttendance;
  }
}
