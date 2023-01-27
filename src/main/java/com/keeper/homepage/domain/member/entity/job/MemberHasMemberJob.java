package com.keeper.homepage.domain.member.entity.job;

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
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"memberJob"})
@Table(name = "member_has_member_job")
public class MemberHasMemberJob {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false, updatable = false)
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_job_id", nullable = false, updatable = false)
  private MemberJob memberJob;

  @Builder
  private MemberHasMemberJob(Member member, MemberJob memberJob) {
    this.member = member;
    this.memberJob = memberJob;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MemberHasMemberJob memberHasMemberJob = (MemberHasMemberJob) o;
    return Objects.equals(member.getId(), memberHasMemberJob.getMember().getId()) &&
        Objects.equals(memberJob.getId(), memberHasMemberJob.getMemberJob().getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(member.getId(), memberJob.getId());
  }
}
