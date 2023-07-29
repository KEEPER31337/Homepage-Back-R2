package com.keeper.homepage.domain.ctf.entity.team;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.member.entity.Member;
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
@EqualsAndHashCode(of = {"ctfTeam", "member"})
@IdClass(CtfTeamHasMemberPK.class)
@Table(name = "ctf_team_has_member")
public class CtfTeamHasMember {

  @Id
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "ctf_team_id", nullable = false, updatable = false)
  private CtfTeam ctfTeam;

  @Id
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_id", nullable = false, updatable = false)
  private Member member;

  @Builder
  private CtfTeamHasMember(CtfTeam ctfTeam, Member member) {
    this.ctfTeam = ctfTeam;
    this.member = member;
  }
}
