package com.keeper.homepage.domain.merit.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "merit_type")
public class MeritType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "merit", nullable = false)
  private Integer merit;

  @Column(name = "detail", nullable = false)
  private String detail;

  @Column(name = "is_merit", nullable = false)
  private Boolean isMerit;

  @Builder
  public MeritType(Integer merit, String detail, boolean isMerit) {
    this.merit = merit;
    this.detail = detail;
    this.isMerit = isMerit;
  }

  public void update(Integer score, String reason, boolean isMerit) {
    this.merit = score;
    this.detail = reason;
    this.isMerit = isMerit;
  }
}
