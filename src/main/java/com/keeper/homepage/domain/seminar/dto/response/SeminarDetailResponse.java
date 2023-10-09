package com.keeper.homepage.domain.seminar.dto.response;

import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SeminarDetailResponse {

  private Long id;
  private String name;
  private LocalDateTime openTime;
  private LocalDateTime attendanceStartTime;
  private LocalDateTime attendanceCloseTime;
  private LocalDateTime latenessCloseTime;
  private String statusType;
  private String attendanceCode;
  private Long starterId;

  public static SeminarDetailResponse from(Seminar seminar, SeminarAttendanceStatusType seminarAttendanceStatusType) {
    return SeminarDetailResponse.builder()
        .id(seminar.getId())
        .name(seminar.getName())
        .openTime(seminar.getOpenTime())
        .attendanceStartTime(seminar.getAttendanceStartTime())
        .attendanceCloseTime(seminar.getAttendanceCloseTime())
        .latenessCloseTime(seminar.getLatenessCloseTime())
        .statusType(seminarAttendanceStatusType.toString())
        .attendanceCode(seminar.getAttendanceCode())
        .starterId(seminar.getStarter() != null ? seminar.getStarter().getId() : null)
        .build();
  }
}
