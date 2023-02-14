package com.keeper.homepage.domain.study.entity;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class StudyHasMemberPK implements Serializable {

  private Long study;
  private Long member;
}
