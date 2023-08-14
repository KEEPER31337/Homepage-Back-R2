package com.keeper.homepage.domain.ctf.entity.challenge;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@ToString(of = {"type"})
@EqualsAndHashCode(of = {"id"})
@Table(name = "ctf_challenge_type")
public class CtfChallengeType {

  private static final int MAX_NAME_LENGTH = 45;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
  private ChallengeType type;

  public static CtfChallengeType getCtfChallengeType(ChallengeType type) {
    return CtfChallengeType.builder()
        .id(type.id)
        .type(type)
        .build();
  }

  @Builder
  private CtfChallengeType(Long id, ChallengeType type) {
    this.id = id;
    this.type = type;
  }

  @Getter
  @RequiredArgsConstructor
  public enum ChallengeType {
    STANDARD(1),
    DYNAMIC(2),
    ;

    private final long id;
  }
}
