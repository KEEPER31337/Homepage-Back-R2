package com.keeper.homepage.domain.merit.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "merit_log")
public class MeritLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "member_id", nullable = false)
  private Long memberId;

  @Column(name = "member_realname", nullable = false)
  private String memberRealName;

  @Column(name = "member_generation", nullable = false)
  private String memberGeneration;

  @Column(name = "time", nullable = false)
  private LocalDateTime time;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "merit_type_id")
  private MeritType meritType;

  @Builder
  public MeritLog(Long memberId, String memberRealName, String memberGeneration,
      MeritType meritType) {
    this.memberId = memberId;
    this.memberRealName = memberRealName;
    this.memberGeneration = memberGeneration;
    this.time = LocalDateTime.now();
    this.meritType = meritType;
  }
}
