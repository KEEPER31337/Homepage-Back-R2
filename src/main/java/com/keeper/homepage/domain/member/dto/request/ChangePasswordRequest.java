package com.keeper.homepage.domain.member.dto.request;

import static com.keeper.homepage.domain.member.entity.embedded.Password.PASSWORD_REGEX;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.embedded.Password;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PACKAGE)
public class ChangePasswordRequest {

  @NotEmpty
  private String oldPassword;

  @Pattern(regexp = PASSWORD_REGEX, message = Password.PASSWORD_INVALID)
  private String newPassword;

  public static ChangePasswordRequest from(String oldPassword, String newPassword) {
    return new ChangePasswordRequest(oldPassword, newPassword);
  }
}
