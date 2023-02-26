package com.keeper.homepage.domain.member.entity.comment;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class MemberHasCommentPK implements Serializable {

  private Long member;
  private Long comment;
}
