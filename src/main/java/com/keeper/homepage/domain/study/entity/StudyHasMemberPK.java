package com.keeper.homepage.domain.study.entity;

import com.keeper.homepage.domain.member.entity.Member;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class StudyHasMemberPK implements Serializable {

  @ManyToOne(targetEntity = Study.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "study_id", nullable = false, updatable = false)
  private Study study;

  @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false, updatable = false)
  private Member member;
}
