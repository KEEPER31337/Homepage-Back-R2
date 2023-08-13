package com.keeper.homepage.domain.ctf.entity.challenge;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.ctf.converter.CtfChallengeCategoryTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Arrays;
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
@Table(name = "ctf_challenge_category")
public class CtfChallengeCategory {

  private static final int MAX_NAME_LENGTH = 45;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Convert(converter = CtfChallengeCategoryTypeConverter.class)
  @Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
  private CtfChallengeCategoryType type;

  public static CtfChallengeCategory getCtfChallengeCategoryBy(CtfChallengeCategoryType type) {
    return CtfChallengeCategory.builder()
        .id(type.id)
        .type(type)
        .build();
  }

  @Builder
  private CtfChallengeCategory(Long id, CtfChallengeCategoryType type) {
    this.id = id;
    this.type = type;
  }

  @Getter
  @RequiredArgsConstructor
  public enum CtfChallengeCategoryType {
    MISC(1, "Misc"),
    SYSTEM(2, "System"),
    REVERSING(3, "Reversing"),
    FORENSIC(4, "Forensic"),
    WEB(5, "Web"),
    CRYPTO(6, "Crypto"),
    OSINT(7, "OSINT"),
    ;

    private final long id;
    private final String type;

    public static CtfChallengeCategoryType fromCode(String type) {
      return Arrays.stream(CtfChallengeCategoryType.values())
          .filter(EnumType -> EnumType.getType().equals(type))
          .findAny()
          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 CTF 카테고리 타입입니다."));
    }
  }
}
