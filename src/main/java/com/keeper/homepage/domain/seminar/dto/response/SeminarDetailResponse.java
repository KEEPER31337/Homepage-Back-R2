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

  private Long seminarId;
  private String seminarName;
  private LocalDateTime openTime;
  private LocalDateTime attendanceCloseTime;
  private LocalDateTime latenessCloseTime;
  private String statusType;
  private String attendanceCode;

  public static SeminarDetailResponse from(Seminar seminar, SeminarAttendanceStatusType seminarAttendanceStatusType) {
    return SeminarDetailResponse.builder()
        .seminarId(seminar.getId())
        .seminarName(seminar.getName())
        .openTime(seminar.getOpenTime())
        .attendanceCloseTime(seminar.getAttendanceCloseTime())
        .latenessCloseTime(seminar.getLatenessCloseTime())
        .statusType(seminarAttendanceStatusType.toString())
        .attendanceCode(seminar.getAttendanceCode())
        .build();
  }
}
