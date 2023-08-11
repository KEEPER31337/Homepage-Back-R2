package com.keeper.homepage.domain.merit.dto.request;


import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.merit.entity.MeritType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class GiveMeritPointRequest {

  @NotNull(message = "수여자를 입력해주세요.")
  private long awarderId;

  @NotNull(message = "상벌점 사유를 선택해주세요.")
  private long meritTypeId;

  public GiveMeritPointRequest from(Member member, MeritType meritType) {
    return GiveMeritPointRequest.builder()
        .awarderId(member.getId())
        .meritTypeId(meritType.getId())
        .build();
  }

}
