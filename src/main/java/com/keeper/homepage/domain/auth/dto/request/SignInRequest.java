package com.keeper.homepage.domain.auth.dto.request;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PACKAGE)
@Builder
public class SignInRequest {

  private String loginId;
  @JsonProperty("password")
  private String rawPassword;
}
