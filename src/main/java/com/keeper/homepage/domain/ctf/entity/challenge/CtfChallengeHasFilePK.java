package com.keeper.homepage.domain.ctf.entity.challenge;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class CtfChallengeHasFilePK implements Serializable {

  private Long ctfChallenge;
  private Long file;

}
