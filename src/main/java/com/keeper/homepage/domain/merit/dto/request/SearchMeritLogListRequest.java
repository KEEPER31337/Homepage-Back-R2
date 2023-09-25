package com.keeper.homepage.domain.merit.dto.request;

import static lombok.AccessLevel.*;

import com.keeper.homepage.domain.merit.entity.MeritLog;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class SearchMeritLogListRequest {

  @Pattern(regexp = "^(MERIT|DEMERIT|ALL)$", message = "조회 타입을 올바르게 입력해주세요.")
  private String meritType;

  public static SearchMeritLogListRequest from(String meritType) {
    return new SearchMeritLogListRequest(meritType);
  }

}
