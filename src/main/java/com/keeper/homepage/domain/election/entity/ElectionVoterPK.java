package com.keeper.homepage.domain.election.entity;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class ElectionVoterPK implements Serializable {

  private Long member;
  private Long election;

}
