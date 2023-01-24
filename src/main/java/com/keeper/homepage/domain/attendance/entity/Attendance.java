package com.keeper.homepage.domain.attendance.entity;

import com.keeper.homepage.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "attendance")
public class Attendance {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "time", nullable = false, updatable = false)
  private LocalDateTime time;

  @Column(name = "date", nullable = false, updatable = false)
  private LocalDate date;

  @Column(name = "point", nullable = false, updatable = false)
  private int point;

  @Column(name = "random_point", nullable = false, updatable = false)
  private int randomPoint;

  @Column(name = "rank_point", nullable = false, updatable = false)
  private int rankPoint;

  @Column(name = "continuous_point", nullable = false, updatable = false)
  private int continuousPoint;

  @Column(name = "ip_address", nullable = false, updatable = false, length = 128)
  private String ipAddress;

  @Column(name = "greetings", updatable = false, length = 250)
  private String greetings;

  @Column(name = "continuous_day", nullable = false, updatable = false)
  private int continuousDay;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false, updatable = false)
  private Member member;

  @Column(name = "rank")
  private Integer rank;

  @Builder
  private Attendance(int point, int randomPoint, int rankPoint, int continuousPoint,
      String ipAddress, String greetings, int continuousDay, Member member, Integer rank) {
    this.point = point;
    this.randomPoint = randomPoint;
    this.rankPoint = rankPoint;
    this.continuousPoint = continuousPoint;
    this.ipAddress = ipAddress;
    this.greetings = greetings;
    this.continuousDay = continuousDay;
    this.member = member;
    this.rank = rank;
  }
}
