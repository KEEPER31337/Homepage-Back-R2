package com.keeper.homepage.domain.seminar.entity;

import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.LATENESS;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.getSeminarAttendanceStatusBy;

import com.keeper.homepage.domain.member.entity.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Optional;
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
@Table(name = "seminar_attendance",
    indexes = @Index(name = "is_seminar_attendance_duplicated", columnList = "seminar_id, member_id", unique = true))
public class SeminarAttendance {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seminar_id")
  private Seminar seminar;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "status_id")
  private SeminarAttendanceStatus seminarAttendanceStatus;

  @OneToOne(mappedBy = "seminarAttendance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private SeminarAttendanceExcuse seminarAttendanceExcuse;

  @Column(name = "attend_time", nullable = false, updatable = false)
  private LocalDateTime attendTime;

  public Optional<String> getExcuse() {
    if (this.getSeminarAttendanceExcuse() == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(this.getSeminarAttendanceExcuse().getAbsenceExcuse());
  }

  public void changeLatenessStatus(String excuse) {
    seminarAttendanceStatus = getSeminarAttendanceStatusBy(LATENESS);
    if (seminarAttendanceExcuse == null) {
      seminarAttendanceExcuse = SeminarAttendanceExcuse.builder()
          .seminarAttendance(this)
          .absenceExcuse(excuse)
          .build();
    } else {
      seminarAttendanceExcuse.changeAbsenceExcuse(excuse);
    }
  }

  @Builder
  private SeminarAttendance(Seminar seminar, Member member,
      SeminarAttendanceStatus seminarAttendanceStatus, LocalDateTime attendTime) {
    this.seminar = seminar;
    this.member = member;
    this.seminarAttendanceStatus = seminarAttendanceStatus;
    this.attendTime = attendTime;
  }
}
