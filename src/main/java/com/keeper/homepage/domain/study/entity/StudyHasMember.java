package com.keeper.homepage.domain.study.entity;

import com.keeper.homepage.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Builder
@Table(name = "study_has_member")
public class StudyHasMember implements Serializable {

  @EmbeddedId
  private StudyHasMemberPK studyHasMemberPK;

  @Column(name = "register_time", nullable = false, updatable = false)
  private LocalDateTime registerTime;

  @Builder
  private StudyHasMember(StudyHasMemberPK studyHasMemberPK, LocalDateTime registerTime) {
    this.studyHasMemberPK = studyHasMemberPK;
    this.registerTime = registerTime;
  }
}
