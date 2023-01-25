package com.keeper.homepage.domain.attendance.entity;

import com.keeper.homepage.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "attendance",
    indexes = @Index(name = "is_duplicated", columnList = "member_id, date", unique = true))
public class Attendance {

  public static final int MAX_IP_ADDRESS_LENGTH = 128;
  public static final int MAX_GREETINGS_LENGTH = 250;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "time", nullable = false, updatable = false)
  private LocalDateTime time;

  @Column(name = "date", nullable = false, updatable = false)
  private LocalDate date;

  @Column(name = "point", nullable = false, updatable = false)
  private Integer point;

  @Column(name = "random_point", nullable = false, updatable = false)
  private Integer randomPoint;

  @Column(name = "rank_point", nullable = false, updatable = false)
  private Integer rankPoint;

  @Column(name = "continuous_point", nullable = false, updatable = false)
  private Integer continuousPoint;

  @Column(name = "ip_address", nullable = false, updatable = false, length = MAX_IP_ADDRESS_LENGTH)
  private String ipAddress;

  @Column(name = "greetings", updatable = false, length = MAX_GREETINGS_LENGTH)
  private String greetings;

  @Column(name = "continuous_day", nullable = false, updatable = false)
  private Integer continuousDay;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false, updatable = false)
  private Member member;

  @Column(name = "`rank`")
  private Integer rank;

  @Builder
  private Attendance(LocalDateTime time, LocalDate date, Integer point, Integer randomPoint,
      Integer rankPoint,
      Integer continuousPoint, String ipAddress, String greetings, Integer continuousDay,
      Member member,
      Integer rank) {
    this.time = time;
    this.date = date;
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
