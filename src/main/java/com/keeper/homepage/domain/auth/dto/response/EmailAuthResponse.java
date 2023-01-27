package com.keeper.homepage.domain.auth.dto.response;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class EmailAuthResponse {

  private int expiredSeconds;

  public static EmailAuthResponse from(int expiredSeconds) {
    return new EmailAuthResponse(expiredSeconds);
  }
}
