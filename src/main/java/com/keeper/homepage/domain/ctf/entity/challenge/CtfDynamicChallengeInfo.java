package com.keeper.homepage.domain.ctf.entity.challenge;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "ctf_dynamic_challenge_info")
public class CtfDynamicChallengeInfo {

  @Id
  private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "challenge_id", nullable = false)
  private CtfChallenge ctfChallenge;

  @Column(name = "min_score", nullable = false)
  private Integer minScore;

  @Column(name = "max_score", nullable = false)
  private Integer maxScore;

  @Builder
  private CtfDynamicChallengeInfo(CtfChallenge ctfChallenge, Integer minScore, Integer maxScore) {
    this.ctfChallenge = ctfChallenge;
    this.minScore = minScore;
    this.maxScore = maxScore;
  }

  public void setCtfChallenge(CtfChallenge ctfChallenge) {
    this.ctfChallenge = ctfChallenge;
  }
}
