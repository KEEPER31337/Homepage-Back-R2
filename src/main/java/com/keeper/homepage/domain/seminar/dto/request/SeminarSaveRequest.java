package com.keeper.homepage.domain.seminar.dto.request;

import static java.util.stream.Collectors.joining;
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
public class SeminarSaveRequest {

  private LocalDateTime attendanceCloseTime;
  private LocalDateTime latenessCloseTime;

  public Seminar toEntity(String attendanceCode) {
    return Seminar.builder()
        .attendanceCloseTime(attendanceCloseTime)
        .latenessCloseTime(latenessCloseTime)
        .attendanceCode(attendanceCode)
        .build();
  }
}
