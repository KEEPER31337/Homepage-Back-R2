package com.keeper.homepage.domain.vote.entity;

import static lombok.AccessLevel.PROTECTED;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class VoteChoiceId implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private UUID receipt;
  private Long option;
}
