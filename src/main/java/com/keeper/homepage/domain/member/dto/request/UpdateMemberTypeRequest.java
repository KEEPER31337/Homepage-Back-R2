package com.keeper.homepage.domain.member.dto.request;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class UpdateMemberTypeRequest {

  @NotEmpty(message = "하나 이상의 회원 ID를 입력해주세요.")
  @UniqueElements(message = "중복된 회원이 있습니다.")
  private List<Long> memberIds;

}
