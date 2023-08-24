package com.keeper.homepage.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateMemberEmailAddressRequest {

  @NotBlank(message = "이메일을 입력해주세요.")
  @Email(message = "이메일 형식을 확인해주세요.")
  private String email;
}
