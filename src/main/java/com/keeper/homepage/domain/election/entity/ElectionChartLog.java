package com.keeper.homepage.domain.election.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "election_chart_log")
public class ElectionChartLog {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "vote_time", nullable = false)
  private LocalDateTime voteTime;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "election_candidate_id", nullable = false)
  private ElectionCandidate electionCandidate;

  @Builder
  private ElectionChartLog(LocalDateTime voteTime, ElectionCandidate electionCandidate) {
    this.voteTime = voteTime;
    this.electionCandidate = electionCandidate;
  }

}
