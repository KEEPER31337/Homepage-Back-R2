package com.keeper.homepage.domain.ctf.entity.challenge;

import static com.keeper.homepage.domain.ctf.entity.challenge.CtfChallengeCategory.getCtfChallengeCategoryBy;
import static com.keeper.homepage.domain.ctf.entity.challenge.CtfChallengeType.ChallengeType.DYNAMIC;
import static com.keeper.homepage.domain.ctf.entity.challenge.CtfChallengeType.getCtfChallengeType;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.ctf.entity.CtfContest;
import com.keeper.homepage.domain.ctf.entity.CtfFlag;
import com.keeper.homepage.domain.ctf.entity.challenge.CtfChallengeCategory.CtfChallengeCategoryType;
import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Table(name = "ctf_challenge")
public class CtfChallenge extends BaseEntity {

  private static final int MAX_NAME_LENGTH = 200;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "creator", nullable = false)
  private Member creator;

  @Column(name = "is_solvable", nullable = false)
  private Boolean isSolvable;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "type_id")
  private CtfChallengeType type;

  @Column(name = "score", nullable = false)
  private Integer score;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "contest_id", nullable = false)
  private CtfContest ctfContest;

  @Column(name = "max_submit_count", nullable = false)
  private Integer maxSubmitCount;

  @OneToOne(mappedBy = "ctfChallenge", cascade = ALL, fetch = LAZY)
  private CtfDynamicChallengeInfo ctfDynamicChallengeInfo;

  @OneToMany(mappedBy = "ctfChallenge", cascade = ALL, orphanRemoval = true)
  private final Set<CtfChallengeHasCtfChallengeCategory> ctfCategories = new HashSet<>();

  @OneToMany(mappedBy = "ctfChallenge", cascade = ALL, orphanRemoval = true)
  private final Set<CtfChallengeHasFile> ctfFiles = new HashSet<>();

  @OneToMany(mappedBy = "ctfChallenge", cascade = REMOVE)
  private final List<CtfFlag> ctfFlags = new ArrayList<>();

  @Builder
  private CtfChallenge(String name, String description, Member creator, Boolean isSolvable,
      CtfChallengeType type, Integer score, CtfContest ctfContest, Integer maxSubmitCount) {
    this.name = name;
    this.description = description;
    this.creator = creator;
    this.isSolvable = isSolvable;
    this.type = type;
    this.score = score;
    this.ctfContest = ctfContest;
    this.maxSubmitCount = maxSubmitCount;
  }

  public void addCategory(CtfChallengeCategoryType categoryType) {
    this.ctfCategories.add(CtfChallengeHasCtfChallengeCategory.builder()
        .ctfChallenge(this)
        .ctfChallengeCategory(getCtfChallengeCategoryBy(categoryType))
        .build());
  }

  public void addFile(FileEntity file) {
    this.ctfFiles.add(CtfChallengeHasFile.builder()
        .ctfChallenge(this)
        .file(file)
        .build());
  }

  public void setCtfDynamicChallengeInfo(CtfDynamicChallengeInfo ctfDynamicChallengeInfo) {
    if (!type.equals(getCtfChallengeType(DYNAMIC))) {
      throw new IllegalStateException("CTF 문제 타입이 DYNAMIC이 아니므로 설정할 수 없습니다.");
    }
    this.ctfDynamicChallengeInfo = ctfDynamicChallengeInfo;
    ctfDynamicChallengeInfo.setCtfChallenge(this);
  }

  public void changeCreator(Member member) {
    this.creator = member;
  }
}
