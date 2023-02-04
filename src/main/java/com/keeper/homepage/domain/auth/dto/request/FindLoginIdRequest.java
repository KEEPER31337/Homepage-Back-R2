package com.keeper.homepage.domain.auth.dto.request;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PACKAGE)
public class FindLoginIdRequest {

  @Email
  private String email;

  public static FindLoginIdRequest from(String email) {
    return new FindLoginIdRequest(email);
  }
}
