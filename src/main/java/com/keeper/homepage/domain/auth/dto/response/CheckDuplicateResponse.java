package com.keeper.homepage.domain.auth.dto.response;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class CheckDuplicateResponse {

  private boolean isDuplicate;

  public static CheckDuplicateResponse from(boolean isDuplicate) {
    return new CheckDuplicateResponse(isDuplicate);
  }
}
