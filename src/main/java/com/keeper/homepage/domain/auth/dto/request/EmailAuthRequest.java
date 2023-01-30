package com.keeper.homepage.domain.auth.dto.request;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class EmailAuthRequest {

  @Email
  private String email;

  public static EmailAuthRequest from(String email) {
    return new EmailAuthRequest(email);
  }
}
