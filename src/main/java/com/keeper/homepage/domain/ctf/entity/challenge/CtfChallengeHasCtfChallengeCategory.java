package com.keeper.homepage.domain.ctf.entity.challenge;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = {"ctfChallenge", "ctfChallengeCategory"})
@IdClass(CtfChallengeHasCtfChallengeCategoryPK.class)
@Table(name = "ctf_challenge_has_ctf_challenge_category")
public class CtfChallengeHasCtfChallengeCategory {

  @Id
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "ctf_challenge_id", nullable = false, updatable = false)
  private CtfChallenge ctfChallenge;

  @Id
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "ctf_challenge_category_id", nullable = false, updatable = false)
  private CtfChallengeCategory ctfChallengeCategory;

  @Builder
  private CtfChallengeHasCtfChallengeCategory(CtfChallenge ctfChallenge, CtfChallengeCategory ctfChallengeCategory) {
    this.ctfChallenge = ctfChallenge;
    this.ctfChallengeCategory = ctfChallengeCategory;
  }
}
