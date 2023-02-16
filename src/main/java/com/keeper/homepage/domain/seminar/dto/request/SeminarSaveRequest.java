package com.keeper.homepage.domain.seminar.dto.request;

import static java.util.stream.Collectors.joining;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import jakarta.validation.constraints.Future;
import java.time.LocalDateTime;
import java.util.Random;
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

  private static final Random RANDOM = new Random();

  public Seminar toEntity() {
    return Seminar.builder()
        .attendanceCloseTime(attendanceCloseTime)
        .latenessCloseTime(latenessCloseTime)
        .attendanceCode(randomAttendanceCode())
        .build();
  }

  private String randomAttendanceCode() {
    final int ATTENDANCE_CODE_LENGTH = 4;

    return RANDOM.ints(ATTENDANCE_CODE_LENGTH, 1, 10)
        .mapToObj(i -> ((Integer) i).toString())
        .collect(joining());
  }
}
