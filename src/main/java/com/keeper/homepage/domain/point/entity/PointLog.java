package com.keeper.homepage.domain.point.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "point_log")
public class PointLog {

  private static final int MAX_DETAIL_LENGTH = 45;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "time", nullable = false)
  private LocalDateTime time;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_id", nullable = false, updatable = false)
  private Member member;

  @Column(name = "point", nullable = false)
  private Integer point;

  @Column(name = "detail", length = MAX_DETAIL_LENGTH)
  private String detail;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "presented", updatable = false)
  private Member presented;

  @Column(name = "is_spent", nullable = false)
  private Boolean isSpent;

  @Builder
  private PointLog(LocalDateTime time, Member member, Integer point, String detail,
      Member presented, Boolean isSpent) {
    this.time = time;
    this.member = member;
    this.point = point;
    this.detail = detail;
    this.presented = presented;
    this.isSpent = isSpent != null ? isSpent : false;
  }
}
