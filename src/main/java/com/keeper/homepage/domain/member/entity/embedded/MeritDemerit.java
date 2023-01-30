package com.keeper.homepage.domain.member.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MeritDemerit {

  @Column(name = "merit", nullable = false)
  private Integer merit;

  @Column(name = "demerit", nullable = false)
  private Integer demerit;
}
