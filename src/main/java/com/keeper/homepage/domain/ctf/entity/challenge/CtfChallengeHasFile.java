package com.keeper.homepage.domain.ctf.entity.challenge;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.file.entity.FileEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = {"ctfChallenge", "file"})
@IdClass(CtfChallengeHasFilePK.class)
@Table(name = "ctf_challenge_has_file")
public class CtfChallengeHasFile {

  @Id
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "ctf_challenge_id", nullable = false, updatable = false)
  private CtfChallenge ctfChallenge;

  @Id
  @OneToOne(fetch = LAZY)
  @JoinColumn(name = "file_id", nullable = false, updatable = false)
  private FileEntity file;

  @Builder
  private CtfChallengeHasFile(CtfChallenge ctfChallenge, FileEntity file) {
    this.ctfChallenge = ctfChallenge;
    this.file = file;
  }
}
