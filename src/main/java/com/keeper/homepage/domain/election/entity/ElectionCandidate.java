package com.keeper.homepage.domain.election.entity;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "election_candidate")
public class ElectionCandidate {

  private static final int MAX_DESCRIPTION_LENGTH = 200;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "election_id", nullable = false)
  private Election election;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "candidate_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_job_id", nullable = false)
  private MemberJob memberJob;

  @Column(name = "description", length = MAX_DESCRIPTION_LENGTH)
  private String description;

  @Column(name = "register_time", nullable = false)
  private LocalDateTime registerTime;

  @Column(name = "vote_count", nullable = false)
  private Long voteCount;

  @OneToMany(mappedBy = "electionCandidate", cascade = REMOVE)
  private final List<ElectionChartLog> electionChartLogs = new ArrayList<>();

  @Builder
  private ElectionCandidate(Election election, Member member, MemberJob memberJob,
      String description, Long voteCount) {
    this.election = election;
    this.member = member;
    this.memberJob = memberJob;
    this.registerTime = LocalDateTime.now();
    this.description = description;
    this.voteCount = voteCount;
  }

}
