package com.keeper.homepage.domain.seminar.dto.response;

import com.keeper.homepage.domain.seminar.entity.Seminar;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SeminarResponse {

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
