package com.keeper.homepage.domain.member.dto.request;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class UpdateMemberTypeRequest {

  @Size(min = 1, message = "하나 이상의 회원 ID를 입력해주세요.")
  @NotNull
  private Set<Long> memberIds;

}
