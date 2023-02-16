package com.keeper.homepage.domain.study.entity;

import static jakarta.persistence.FetchType.LAZY;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"study", "member"})
@IdClass(StudyHasMemberPK.class)
@Table(name = "study_has_member")
public class StudyHasMember {

  @Id
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "study_id", nullable = false, updatable = false)
  private Study study;

  @Id
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_id", nullable = false, updatable = false)
  private Member member;

  @Column(name = "register_time", nullable = false, updatable = false)
  private LocalDateTime registerTime;

  @Builder
  private StudyHasMember(Study study, Member member) {
    this.study = study;
    this.member = member;
  }
}
