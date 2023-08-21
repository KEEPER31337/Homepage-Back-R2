package com.keeper.homepage.domain.study.dto.request;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class StudyJoinRequest {

  @Size(min = 1)
  @NotNull(message = "회원의 ID 리스트를 입력해주세요.")
  private List<Long> memberIds;

}
