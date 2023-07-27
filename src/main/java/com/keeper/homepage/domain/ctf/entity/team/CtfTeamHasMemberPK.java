package com.keeper.homepage.domain.ctf.entity.team;

import static lombok.AccessLevel.PROTECTED;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class CtfTeamHasMemberPK implements Serializable {

  private Long ctfTeam;
  private Long member;

}
