package com.keeper.homepage.global.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

  public static ErrorResponse from(String message) {
    return new ErrorResponse(message);
  }

  private final String message;
}
