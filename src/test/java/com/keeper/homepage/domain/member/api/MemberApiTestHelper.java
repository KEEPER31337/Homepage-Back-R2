package com.keeper.homepage.domain.member.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.keeper.homepage.IntegrationTest;
import jakarta.servlet.http.Cookie;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.MultiValueMap;

public class MemberApiTestHelper extends IntegrationTest {

  ResultActions callGetPointRankingApi(String accessToken, MultiValueMap<String, String> params)
      throws Exception {
    return mockMvc.perform(get("/members/point-rank")
        .params(params)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken)));
  }

  FieldDescriptor[] getPointRankResponse() {
    return new FieldDescriptor[]{
        fieldWithPath("realName").description("회원의 실명"),
        fieldWithPath("generation").description("회원의 기수"),
        fieldWithPath("point").description("회원의 포인트")
    };
  }

  FieldDescriptor[] getMemberProfileResponse() {
    return new FieldDescriptor[]{
        fieldWithPath("id").description("Member PK ID"),
        fieldWithPath("emailAddress").description("회원의 이메일 주소"),
        fieldWithPath("realName").description("회원의 실명"),
        fieldWithPath("studentId").description("회원의 학번"),
        fieldWithPath("thumbnailPath").description("썸네일 경로"),
        fieldWithPath("generation").description("회원의 기수"),
        fieldWithPath("point").description("회원의 포인트 점수"),
        fieldWithPath("memberType").description("회원의 타입"),
        fieldWithPath("memberJobs").description("회원의 역할")
    };
  }

}
