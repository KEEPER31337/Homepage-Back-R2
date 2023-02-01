package com.keeper.homepage.domain.clerk.seminar;

import static com.keeper.homepage.domain.clerk.entity.seminar.SeminarAttendanceStatus.SeminarAttendanceStatusType.*;

import com.keeper.homepage.domain.clerk.dao.seminar.SeminarAttendanceRepository;
import com.keeper.homepage.domain.clerk.entity.seminar.Seminar;
import com.keeper.homepage.domain.clerk.entity.seminar.SeminarAttendance;
import com.keeper.homepage.domain.clerk.entity.seminar.SeminarAttendanceStatus;
import com.keeper.homepage.domain.clerk.entity.seminar.SeminarAttendanceStatus.SeminarAttendanceStatusType;
import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SeminarAttendanceTestHelper {

  @Autowired
  SeminarAttendanceRepository seminarAttendanceRepository;

  @Autowired
  MemberTestHelper memberTestHelper;

  @Autowired
  SeminarTestHelper seminarTestHelper;

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
    private LocalDate attendTime;

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

    public SeminarAttendanceBuilder attendTime(LocalDate attendTime) {
      this.attendTime = attendTime;
      return this;
    }

    public SeminarAttendance build() {
      return seminarAttendanceRepository.save(SeminarAttendance.builder()
          .seminar(seminar != null ? seminar : seminarTestHelper.generate())
          .member(member != null ? member : memberTestHelper.builder().build())
          .seminarAttendanceStatus(seminarAttendanceStatus != null ? seminarAttendanceStatus
              : initAttendanceStatus(ATTENDANCE))
          .attendTime(attendTime != null ? attendTime : LocalDate.now()).build());
    }

    private SeminarAttendanceStatus initAttendanceStatus(SeminarAttendanceStatusType statusType) {
      return SeminarAttendanceStatus.builder().type(statusType).build();
    }
  }
}
