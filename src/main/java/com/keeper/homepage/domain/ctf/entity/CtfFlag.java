package com.keeper.homepage.domain.ctf.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.ctf.entity.challenge.CtfChallenge;
import com.keeper.homepage.domain.ctf.entity.team.CtfTeam;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Table(name = "ctf_flag")
public class CtfFlag {

  private static final int MAX_CONTENT_LENGTH = 200;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "content", nullable = false, length = MAX_CONTENT_LENGTH)
  private String content;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "team_id", nullable = false)
  private CtfTeam ctfTeam;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "challenge_id", nullable = false)
  private CtfChallenge ctfChallenge;

  @Column(name = "is_correct", nullable = false)
  private Boolean isCorrect;

  @Column(name = "solved_time")
  private LocalDateTime solvedTime;

  @Column(name = "last_try_time")
  private LocalDateTime lastTryTime;

  @Column(name = "remained_submit_count", nullable = false)
  private Integer remainedSubmitCount;

  @Builder
  private CtfFlag(String content, CtfTeam ctfTeam, CtfChallenge ctfChallenge, Boolean isCorrect,
      LocalDateTime solvedTime, LocalDateTime lastTryTime, Integer remainedSubmitCount) {
    this.content = content;
    this.ctfTeam = ctfTeam;
    this.ctfChallenge = ctfChallenge;
    this.isCorrect = isCorrect;
    this.solvedTime = solvedTime;
    this.lastTryTime = lastTryTime;
    this.remainedSubmitCount = remainedSubmitCount;
  }

  public void decreaseSubmitCount() {
    if (remainedSubmitCount <= 0) {
      throw new IllegalStateException("제출 횟수를 모두 소진하여 제출 횟수를 감소시킬 수 없습니다.");
    }
    --remainedSubmitCount;
  }
}
