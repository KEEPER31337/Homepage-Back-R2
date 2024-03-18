package com.keeper.homepage.domain.ctf.entity.challenge;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@ToString(of = {"ctfChallengeCategory"})
@Table(name = "ctf_challenge_has_ctf_challenge_category")
public class CtfChallengeHasCtfChallengeCategory {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "ctf_challenge_id", nullable = false, updatable = false)
  private CtfChallenge ctfChallenge;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "ctf_challenge_category_id", nullable = false, updatable = false)
  private CtfChallengeCategory ctfChallengeCategory;

  @Builder
  private CtfChallengeHasCtfChallengeCategory(CtfChallenge ctfChallenge,
      CtfChallengeCategory ctfChallengeCategory) {
    this.ctfChallenge = ctfChallenge;
    this.ctfChallengeCategory = ctfChallengeCategory;
  }
}
