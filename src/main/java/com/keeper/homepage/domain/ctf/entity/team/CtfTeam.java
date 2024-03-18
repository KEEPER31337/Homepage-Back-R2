package com.keeper.homepage.domain.ctf.entity.team;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.ctf.entity.CtfContest;
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
import java.time.LocalDateTime;
import java.util.HashSet;
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
@Table(name = "ctf_team")
public class CtfTeam extends BaseEntity {

  private static final int MAX_NAME_LENGTH = 45;
  private static final int MAX_DESCRIPTION_LENGTH = 200;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
  private String name;

  @Column(name = "description", length = MAX_DESCRIPTION_LENGTH)
  private String description;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "creator", nullable = false)
  private Member creator;

  @Column(name = "score", nullable = false)
  private Integer score;

  @OneToOne(fetch = LAZY)
  @JoinColumn(name = "contest_id")
  private CtfContest ctfContest;

  @Column(name = "last_solve_time", nullable = false)
  private LocalDateTime lastSolveTime;

  @OneToMany(mappedBy = "ctfTeam")
  private final Set<CtfTeamHasMember> ctfTeamHasMembers = new HashSet<>();

  @Builder
  private CtfTeam(String name, String description, Member creator, Integer score,
      CtfContest ctfContest,
      LocalDateTime lastSolveTime) {
    this.name = name;
    this.description = description;
    this.creator = creator;
    this.score = score;
    this.ctfContest = ctfContest;
    this.lastSolveTime = lastSolveTime;
  }

  public void update(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public void changeCreator(Member member) {
    this.creator = member;
  }
}
