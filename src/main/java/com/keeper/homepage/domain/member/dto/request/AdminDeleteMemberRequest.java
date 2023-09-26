package com.keeper.homepage.domain.member.dto.request;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class AdminDeleteMemberRequest {

  @NotEmpty(message = "하나 이상의 회원 ID를 입력해주세요.")
  private List<@NotNull Long> memberIds;

}
