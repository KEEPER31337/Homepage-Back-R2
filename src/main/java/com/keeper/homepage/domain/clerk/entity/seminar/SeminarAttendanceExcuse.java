package com.keeper.homepage.domain.clerk.entity.seminar;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seminar_attendance_excuse")
public class SeminarAttendanceExcuse {

  private static final int MAX_ABSENCE_EXCUSE_LENGTH = 200;

  @Id
  private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seminar_attendance_id")
  private SeminarAttendance seminarAttendance;

  @Column(name = "absence_excuse", nullable = false, length = MAX_ABSENCE_EXCUSE_LENGTH)
  private String absenceExcuse;

  public void changeAbsenceExcuse(String excuse) {
    this.absenceExcuse = excuse;
  }

  @Builder
  private SeminarAttendanceExcuse(SeminarAttendance seminarAttendance, String absenceExcuse) {
    this.seminarAttendance = seminarAttendance;
    this.absenceExcuse = absenceExcuse;
  }
}
