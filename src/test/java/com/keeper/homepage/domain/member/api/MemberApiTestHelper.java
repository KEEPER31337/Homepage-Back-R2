package com.keeper.homepage.domain.member.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.dto.request.ProfileUpdateRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.http.MediaType;
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

  ResultActions callUpdateProfileApi(String accessToken, ProfileUpdateRequest request)
      throws Exception {
    return mockMvc.perform(put("/members/profile")
        .content(asJsonString(request))
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken))
        .contentType(MediaType.APPLICATION_JSON));
  }

}
