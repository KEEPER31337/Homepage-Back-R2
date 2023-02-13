package com.keeper.homepage.domain.member.entity.post;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class MemberHasPostingPK implements Serializable {

  private Long member;
  private Long post;
}
