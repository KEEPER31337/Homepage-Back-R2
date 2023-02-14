package com.keeper.homepage.domain.study.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@IdClass(StudyHasMemberPK.class)
@Table(name = "study_has_member")
public class StudyHasMember extends BaseEntity {

  @Id
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "study_id", nullable = false, updatable = false)
  private Study study;

  @Id
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_id", nullable = false, updatable = false)
  private Member member;

  @Builder
  private StudyHasMember(Study study, Member member) {
    this.study = study;
    this.member = member;
  }
}
