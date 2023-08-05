package com.keeper.homepage.domain.ctf.entity.challenge;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class CtfChallengeHasCtfChallengeCategoryPK implements Serializable {

  private Long ctfChallenge;
  private Long ctfChallengeCategory;

}
