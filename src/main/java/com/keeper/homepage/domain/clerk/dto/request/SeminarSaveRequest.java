package com.keeper.homepage.domain.clerk.dto.request;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.clerk.entity.seminar.Seminar;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class SeminarSaveRequest {

  private LocalDateTime attendanceCloseTime;
  private LocalDateTime latenessCloseTime;

  public Seminar toEntity() {
    return Seminar.builder()
        .attendanceCloseTime(attendanceCloseTime)
        .latenessCloseTime(latenessCloseTime)
        .build();
  }
}
