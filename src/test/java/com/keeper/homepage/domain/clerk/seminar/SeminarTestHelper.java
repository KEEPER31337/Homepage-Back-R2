package com.keeper.homepage.domain.clerk.seminar;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import com.keeper.homepage.domain.clerk.dao.seminar.SeminarRepository;
import com.keeper.homepage.domain.clerk.entity.seminar.Seminar;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SeminarTestHelper {

  @Autowired
  SeminarRepository seminarRepository;

  public Seminar generate() {
    return this.builder().build();
  }

  public SeminarBuilder builder() {
    return new SeminarBuilder();
  }

  public final class SeminarBuilder {

    private LocalDateTime openTime;
    private LocalDateTime attendanceCloseTime;
    private LocalDateTime latenessCloseTime;
    private String attendanceCode;
    private String seminarName;

    public SeminarBuilder openTime(LocalDateTime sampleTime) {
      this.openTime = sampleTime;
      return this;
    }

    public SeminarBuilder attendanceCloseTime(LocalDateTime attendanceCloseTime) {
      this.attendanceCloseTime = attendanceCloseTime;
      return this;
    }

    public SeminarBuilder latenessCloseTime(LocalDateTime latenessCloseTime) {
      this.latenessCloseTime = latenessCloseTime;
      return this;
    }

    public SeminarBuilder attendanceCode(String attendanceCode) {
      this.attendanceCode = attendanceCode;
      return this;
    }

    public SeminarBuilder seminarName(String seminarName) {
      this.seminarName = seminarName;
      return this;
    }

    public Seminar build() {
      return seminarRepository.save(Seminar.builder()
          .openTime(openTime)
          .attendanceCloseTime(attendanceCloseTime)
          .latenessCloseTime(latenessCloseTime)
          .attendanceCode(attendanceCode != null ? attendanceCode : randomAttendanceCode())
          .name(seminarName != null ? seminarName : makeSeminarName())
          .registerTime(openTime)
          .updateTime(openTime)
          .build());
    }

    private String randomAttendanceCode() {
      Random r = new Random();
      int ATTENDANCE_CODE_LENGTH = 4;

      return r.ints(ATTENDANCE_CODE_LENGTH, 1, 10)
          .mapToObj(i -> ((Integer) i).toString())
          .collect(joining());
    }

    private String makeSeminarName() {
      LocalDateTime now = LocalDateTime.now();
      return format("%s_세미나", now.format(DateTimeFormatter.ofPattern("yyMMdd")));
    }
  }
}
