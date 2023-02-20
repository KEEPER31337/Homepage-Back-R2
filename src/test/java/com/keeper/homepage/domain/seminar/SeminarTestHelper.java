package com.keeper.homepage.domain.seminar;

import static com.keeper.homepage.IntegrationTest.RANDOM;
import static java.util.stream.Collectors.joining;

import com.keeper.homepage.domain.seminar.dao.SeminarRepository;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import java.time.LocalDateTime;
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
          .name(seminarName)
          .build());
    }

    private String randomAttendanceCode() {
      int ATTENDANCE_CODE_LENGTH = 4;

      return RANDOM.ints(ATTENDANCE_CODE_LENGTH, 1, 10)
          .mapToObj(i -> ((Integer) i).toString())
          .collect(joining());
    }
  }
}
