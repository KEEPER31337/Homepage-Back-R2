package com.keeper.homepage.domain.attendance;

import static com.keeper.homepage.domain.attendance.entity.Attendance.MAX_GREETINGS_LENGTH;

import com.keeper.homepage.domain.attendance.dao.AttendanceRepository;
import com.keeper.homepage.domain.attendance.entity.Attendance;
import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AttendanceTestHelper {

  @Autowired
  AttendanceRepository attendanceRepository;

  @Autowired
  MemberTestHelper memberTestHelper;

  public Attendance generate() {
    return this.builder().build();
  }

  public AttendanceBuilder builder() {
    return new AttendanceBuilder();
  }

  public final class AttendanceBuilder {

    private LocalDateTime time;
    private LocalDate date;
    private Integer point;
    private Integer randomPoint;
    private Integer rankPoint;
    private Integer continuousPoint;
    private String ipAddress;
    private String greetings;
    private Integer continuousDay;
    private Member member;
    private Integer rank;

    private AttendanceBuilder() {
    }

    public AttendanceBuilder time(LocalDateTime time) {
      this.time = time;
      return this;
    }

    public AttendanceBuilder date(LocalDate date) {
      this.date = date;
      return this;
    }

    public AttendanceBuilder point(int point) {
      this.point = point;
      return this;
    }

    public AttendanceBuilder randomPoint(int randomPoint) {
      this.randomPoint = randomPoint;
      return this;
    }

    public AttendanceBuilder rankPoint(int rankPoint) {
      this.rankPoint = rankPoint;
      return this;
    }

    public AttendanceBuilder continuousPoint(int continuousPoint) {
      this.continuousPoint = continuousPoint;
      return this;
    }

    public AttendanceBuilder ipAddress(String ipAddress) {
      this.ipAddress = ipAddress;
      return this;
    }

    public AttendanceBuilder greetings(String greetings) {
      this.greetings = greetings;
      return this;
    }

    public AttendanceBuilder continuousDay(int continuousDay) {
      this.continuousDay = continuousDay;
      return this;
    }

    public AttendanceBuilder member(Member member) {
      this.member = member;
      return this;
    }

    public AttendanceBuilder rank(Integer rank) {
      this.rank = rank;
      return this;
    }

    public Attendance build() {
      return attendanceRepository.save(Attendance.builder()
          .time(time != null ? time : LocalDateTime.now())
          .date(date != null ? date : LocalDate.now())
          .point(point != null ? point : 100)
          .randomPoint(randomPoint != null ? randomPoint : 200)
          .rankPoint(rankPoint != null ? rankPoint : 400)
          .continuousPoint(continuousPoint != null ? continuousPoint : 800)
          .ipAddress(ipAddress != null ? ipAddress : "0.0.0.0")
          .greetings(greetings != null ? greetings : getRandomUUIDLengthWith(MAX_GREETINGS_LENGTH))
          .continuousDay(continuousDay != null ? continuousDay : 0)
          .member(member != null ? member : memberTestHelper.generate())
          .rank(rank != null ? rank : 1)
          .build());
    }

    private static String getRandomUUIDLengthWith(int length) {
      String randomString = UUID.randomUUID().toString();
      length = Math.min(length, randomString.length());
      return randomString.substring(0, length);
    }
  }
}
