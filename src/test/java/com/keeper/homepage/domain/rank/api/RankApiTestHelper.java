package com.keeper.homepage.domain.rank.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.field;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.keeper.homepage.IntegrationTest;
import jakarta.servlet.http.Cookie;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.MultiValueMap;

public class RankApiTestHelper extends IntegrationTest {

  ResultActions callGetPointRankingApi(String accessToken, MultiValueMap<String, String> params)
      throws Exception {
    return mockMvc.perform(get("/ranking/point")
        .params(params)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }

  FieldDescriptor[] getPointRankResponse() {
    return new FieldDescriptor[]{
        fieldWithPath("nickName").description("회원의 닉네임"),
        fieldWithPath("generation").description("회원의 기수"),
        fieldWithPath("point").description("회원의 포인트")
    };
  }

  ResultActions callGetContinuousAttendanceRankingApi(String accessToken)
      throws Exception {
    return mockMvc.perform(get("/ranking/continuous-attendance")
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }

  FieldDescriptor[] getContinuousAttendanceResponse() {
    return new FieldDescriptor[]{
        fieldWithPath("nickname").description("회원의 닉네임"),
        fieldWithPath("generation").description("회원의 기수"),
        fieldWithPath("continuousDay").description("회원의 연속 출석 일수"),
        fieldWithPath("thumbnailPath").description("회원의 썸네일 경로")
    };
  }
}
