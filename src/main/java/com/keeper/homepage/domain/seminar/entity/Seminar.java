package com.keeper.homepage.domain.seminar.entity;

import com.keeper.homepage.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

  public void changeCloseTime(LocalDateTime attendanceCloseTime, LocalDateTime latenessCloseTime) {
    this.attendanceCloseTime = attendanceCloseTime;
    this.latenessCloseTime = latenessCloseTime;
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
}
