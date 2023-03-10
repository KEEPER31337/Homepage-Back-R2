package com.keeper.homepage.domain.seminar.dto.response;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.seminar.entity.Seminar;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PACKAGE)
public class SeminarResponse {

  // TODO: 2023-02-13 front-end와 상의 후 필요한 데이터만 선언하고 나머지는 삭제 예정
  private Long id;
  private LocalDateTime openTime;
  private LocalDateTime attendanceCloseTime;
  private LocalDateTime latenessCloseTime;
  private String attendanceCode;
  private String name;
  private LocalDateTime registerTime;
  private LocalDateTime updateTime;

  public static SeminarResponse from(Seminar seminar) {
    return SeminarResponse.builder()
        .id(seminar.getId())
        .openTime(seminar.getOpenTime())
        .attendanceCloseTime(seminar.getAttendanceCloseTime())
        .latenessCloseTime(seminar.getLatenessCloseTime())
        .attendanceCode(seminar.getAttendanceCode())
        .name(seminar.getName())
        .registerTime(seminar.getRegisterTime())
        .updateTime(seminar.getUpdateTime())
        .build();
  }
}
