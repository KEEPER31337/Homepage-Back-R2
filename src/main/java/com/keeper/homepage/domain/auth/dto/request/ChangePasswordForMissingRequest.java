package com.keeper.homepage.domain.auth.dto.request;

import static com.keeper.homepage.domain.member.entity.embedded.Password.PASSWORD_REGEX;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.keeper.homepage.domain.member.entity.embedded.Password;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PACKAGE)
@Builder
public class ChangePasswordForMissingRequest {

  private String loginId;
  private String email;
  private String authCode;
  @Pattern(regexp = PASSWORD_REGEX, message = Password.PASSWORD_INVALID)
  @JsonProperty("password")
  private String rawPassword;
}
