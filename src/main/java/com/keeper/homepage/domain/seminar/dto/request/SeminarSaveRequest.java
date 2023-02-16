package com.keeper.homepage.domain.seminar.dto.request;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import jakarta.validation.constraints.Future;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PACKAGE)
public class SeminarSaveRequest {

  @Nullable
  @Future
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime attendanceCloseTime;

  @Nullable
  @Future
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime latenessCloseTime;

  public Seminar toEntity(String attendanceCode) {
    return Seminar.builder()
        .attendanceCloseTime(attendanceCloseTime)
        .latenessCloseTime(latenessCloseTime)
        .attendanceCode(attendanceCode)
        .build();
  }
}
