package com.keeper.homepage.domain.auth.dto.response;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class CheckAuthCodeResponse {

  private boolean isAuth;

  public static CheckAuthCodeResponse from(boolean isAuth) {
    return new CheckAuthCodeResponse(isAuth);
  }
}
