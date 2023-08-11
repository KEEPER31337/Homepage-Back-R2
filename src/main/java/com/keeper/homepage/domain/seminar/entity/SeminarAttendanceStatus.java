package com.keeper.homepage.domain.seminar.entity;

import com.keeper.homepage.domain.seminar.converter.SeminarAttendanceStatusTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"type"})
@Table(name = "seminar_attendance_status")
public class SeminarAttendanceStatus {

  private static final int MAX_TYPE_LENGTH = 10;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Convert(converter = SeminarAttendanceStatusTypeConverter.class)
  @Column(name = "type", nullable = false, length = MAX_TYPE_LENGTH)
  private SeminarAttendanceStatusType type;

  public static SeminarAttendanceStatus getSeminarAttendanceStatusBy(
      SeminarAttendanceStatusType type) {
    return SeminarAttendanceStatus.builder()
        .id(type.id)
        .type(type)
        .build();
  }

  @Builder
  private SeminarAttendanceStatus(Long id, SeminarAttendanceStatusType type) {
    this.id = id;
    this.type = type;
  }

  @Getter
  @RequiredArgsConstructor
  public enum SeminarAttendanceStatusType {
    ATTENDANCE(1L, "출석"),
    LATENESS(2L, "지각"),
    ABSENCE(3L, "결석"),
    PERSONAL(4L, "개인사정"),
    BEFORE_ATTENDANCE(5L, "출석 전"),
    ;

    private final Long id;
    private final String type;

    public static SeminarAttendanceStatusType fromCode(String type) {
      return Arrays.stream(SeminarAttendanceStatusType.values())
          .filter(EnumType -> EnumType.getType().equals(type))
          .findAny()
          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 세미나 타입입니다."));
    }
  }
}
