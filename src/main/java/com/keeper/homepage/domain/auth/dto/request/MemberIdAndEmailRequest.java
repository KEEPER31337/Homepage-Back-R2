package com.keeper.homepage.domain.auth.dto.request;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PACKAGE)
@Builder
public class MemberIdAndEmailRequest {

  @Email
  private String email;
  private String loginId;
}
