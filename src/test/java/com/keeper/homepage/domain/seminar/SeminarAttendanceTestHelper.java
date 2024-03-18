package com.keeper.homepage.domain.seminar;

import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.ATTENDANCE;

import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.seminar.dao.SeminarAttendanceRepository;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SeminarAttendanceTestHelper {

  @Autowired
  SeminarAttendanceRepository seminarAttendanceRepository;

  @Autowired
  SeminarTestHelper seminarTestHelper;

  @Autowired
  MemberTestHelper memberTestHelper;

  public SeminarAttendance generate() {
    return this.builder().build();
  }

  public SeminarAttendanceBuilder builder() {
    return new SeminarAttendanceBuilder();
  }

  public final class SeminarAttendanceBuilder {

    private Seminar seminar;
    private Member member;
    private SeminarAttendanceStatus seminarAttendanceStatus;
    private LocalDateTime attendTime;

    public SeminarAttendanceBuilder seminar(Seminar seminar) {
      this.seminar = seminar;
      return this;
    }

    public SeminarAttendanceBuilder member(Member member) {
      this.member = member;
      return this;
    }

    public SeminarAttendanceBuilder seminarAttendanceStatus(
        SeminarAttendanceStatus seminarAttendanceStatus) {
      this.seminarAttendanceStatus = seminarAttendanceStatus;
      return this;
    }

    public SeminarAttendanceBuilder attendTime(LocalDateTime attendTime) {
      this.attendTime = attendTime;
      return this;
    }

    public SeminarAttendance build() {
      return seminarAttendanceRepository.save(SeminarAttendance.builder()
          .seminar(seminar != null ? seminar : seminarTestHelper.generate())
          .member(member != null ? member : memberTestHelper.generate())
          .seminarAttendanceStatus(seminarAttendanceStatus != null ? seminarAttendanceStatus
              : SeminarAttendanceStatus.getSeminarAttendanceStatusBy(ATTENDANCE))
          .attendTime(attendTime != null ? attendTime : LocalDateTime.now())
          .build());
    }
  }

}
