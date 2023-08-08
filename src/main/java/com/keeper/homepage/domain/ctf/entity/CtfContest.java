package com.keeper.homepage.domain.ctf.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Table(name = "ctf_contest")
public class CtfContest extends BaseEntity {

  private static final int MAX_NAME_LENGTH = 45;
  private static final int MAX_DESCRIPTION_LENGTH = 45;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
  private String name;

  @Column(name = "description", length = MAX_DESCRIPTION_LENGTH)
  private String description;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "creator", nullable = false)
  private Member creator;

  @Column(name = "is_joinable", nullable = false)
  private Boolean isJoinable;

  @Builder
  private CtfContest(String name, String description, Member creator, Boolean isJoinable) {
    this.name = name;
    this.description = description;
    this.creator = creator;
    this.isJoinable = isJoinable;
  }

  public void update(String name, String description, boolean isJoinable) {
    this.name = name;
    this.description = description;
    this.isJoinable = isJoinable;
  }

  public void open() {
    this.isJoinable = true;
  }

  public void close() {
    this.isJoinable = false;
  }
}
