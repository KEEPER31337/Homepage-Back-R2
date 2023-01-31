package com.keeper.homepage.domain.clerk.seminar;

import com.keeper.homepage.domain.clerk.dao.seminar.SeminarRepository;
import com.keeper.homepage.domain.clerk.entity.seminar.Seminar;
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

    private LocalDateTime sampleTime;
    private LocalDateTime attendanceCloseTime;
    private LocalDateTime latenessCloseTime;
    private String attendanceCode;
    private String seminarName;

    private SeminarBuilder() {
      sampleTime = LocalDateTime.of(2022, 12, 20, 18, 12, 34);
      attendanceCloseTime = sampleTime.plusMinutes(60);
      latenessCloseTime = sampleTime.plusMinutes(5);
      attendanceCode = "1234";
      seminarName = "세미나 출석 체크";
    }

    public SeminarBuilder sampleTime(LocalDateTime sampleTime) {
      this.sampleTime = sampleTime;
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
          .openTime(sampleTime)
          .attendanceCloseTime(attendanceCloseTime)
          .latenessCloseTime(latenessCloseTime)
          .attendanceCode(attendanceCode)
          .name(seminarName)
          .registerTime(sampleTime)
          .updateTime(sampleTime)
          .build());
    }
  }
}
