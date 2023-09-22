package com.keeper.homepage.domain.member.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteMemberRequest {

  @NotEmpty(message = "비밀번호를 입력해주세요.")
  private String rawPassword;

  public static DeleteMemberRequest from(String rawPassword) {
    return new DeleteMemberRequest(rawPassword);
  }
}
