package com.keeper.homepage.domain.member.entity.posting;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class MemberHasPostingPK implements Serializable {

  private Long member;
  private Long posting;
}
