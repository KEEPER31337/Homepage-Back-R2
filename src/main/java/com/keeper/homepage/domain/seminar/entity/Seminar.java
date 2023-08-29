package com.keeper.homepage.domain.seminar.entity;

import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.ABSENCE;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.ATTENDANCE;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.LATENESS;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.getSeminarAttendanceStatusBy;
import static jakarta.persistence.FetchType.LAZY;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "seminar")
public class Seminar extends BaseEntity {

  private static final int MAX_ATTENDANCE_CODE_LENGTH = 10;
  private static final int MAX_NAME_LENGTH = 100;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "open_time", nullable = false, updatable = false)
  private LocalDateTime openTime;

  @Column(name = "attendance_close_time")
  private LocalDateTime attendanceCloseTime;

  @Column(name = "lateness_close_time")
  private LocalDateTime latenessCloseTime;

  @Column(name = "attendance_code", length = MAX_ATTENDANCE_CODE_LENGTH)
  private String attendanceCode;

  @Column(name = "name", length = MAX_NAME_LENGTH)
  private String name;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "starter_id")
  private Member starter;

  public void changeCloseTime(LocalDateTime attendanceCloseTime, LocalDateTime latenessCloseTime) {
    this.attendanceCloseTime = attendanceCloseTime;
    this.latenessCloseTime = latenessCloseTime;
  }

  public SeminarAttendanceStatus getStatus() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime attendanceCloseTime = this.getAttendanceCloseTime();
    LocalDateTime latenessCloseTime = this.getLatenessCloseTime();

    if (now.isBefore(attendanceCloseTime)) {
      return getSeminarAttendanceStatusBy(ATTENDANCE);
    }

    if (now.isAfter(attendanceCloseTime) && now.isBefore(latenessCloseTime)) {
      return getSeminarAttendanceStatusBy(LATENESS);
    }

    return getSeminarAttendanceStatusBy(ABSENCE);
  }

  @Builder
  private Seminar(LocalDateTime openTime, LocalDateTime attendanceCloseTime,
      LocalDateTime latenessCloseTime, String attendanceCode, String name) {
    this.openTime = openTime;
    this.attendanceCloseTime = attendanceCloseTime;
    this.latenessCloseTime = latenessCloseTime;
    this.attendanceCode = attendanceCode;
    this.name = name;
  }

  public void setStarter(Member starter) {
    this.starter = starter;
  }
}
