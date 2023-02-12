package com.keeper.homepage.domain.seminar.dto.response;

import com.keeper.homepage.domain.seminar.entity.Seminar;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SeminarResponse {

  // TODO: 2023-02-13 front-end와 이야기해서 필요한 데이터만 선언하고 나머지는 삭제 예정
  private Long id;
  private LocalDateTime openTime;
  private LocalDateTime attendanceCloseTime;
  private LocalDateTime latenessCloseTime;
  private String attendanceCode;
  private String name;
  private LocalDateTime registerTime;
  private LocalDateTime updateTime;

  public SeminarResponse(Seminar seminar) {
    this.id = seminar.getId();
    this.openTime = seminar.getOpenTime();
    this.attendanceCloseTime = seminar.getAttendanceCloseTime();
    this.latenessCloseTime = seminar.getLatenessCloseTime();
    this.attendanceCode = seminar.getAttendanceCode();
    this.name = seminar.getName();
    this.registerTime = seminar.getRegisterTime();
    this.updateTime = seminar.getUpdateTime();
  }
}
