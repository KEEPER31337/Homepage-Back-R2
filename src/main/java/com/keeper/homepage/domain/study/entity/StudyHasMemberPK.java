package com.keeper.homepage.domain.study.entity;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor()
public class StudyHasMemberPK implements Serializable {

  private Long study;
  private Long member;
}
