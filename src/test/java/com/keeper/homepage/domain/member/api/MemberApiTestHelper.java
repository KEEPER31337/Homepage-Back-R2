package com.keeper.homepage.domain.member.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
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
        fieldWithPath("memberId").description("회원의 ID"),
        fieldWithPath("realName").description("회원의 실명"),
        fieldWithPath("generation").description("회원의 기수"),
        fieldWithPath("point").description("회원의 포인트"),
        fieldWithPath("thumbnailPath").description("회원의 썸네일 주소")
    };
  }

  FieldDescriptor[] getMemberProfileResponse() {
    return new FieldDescriptor[]{
        fieldWithPath("id").description("Member PK ID"),
        fieldWithPath("emailAddress").description("회원의 이메일 주소"),
        fieldWithPath("realName").description("회원의 실명"),
        fieldWithPath("birthday").description("회원의 생일"),
        fieldWithPath("thumbnailPath").description("썸네일 경로"),
        fieldWithPath("studentId").description("회원의 학번(본인이 아닐경우 default)"),
        fieldWithPath("generation").description("회원의 기수"),
        fieldWithPath("point").description("회원의 포인트 점수"),
        fieldWithPath("memberType").description("회원의 타입"),
        fieldWithPath("memberJobs").description("회원의 역할"),
        fieldWithPath("follower[].id").description("나를 팔로우 하는 회원의 ID"),
        fieldWithPath("follower[].name").description("나를 팔로우 하는 회원의 이름"),
        fieldWithPath("follower[].thumbnailPath").description("나를 팔로우 하는 회원의 썸네일 경로"),
        fieldWithPath("follower[].generation").description("나를 팔로우 하는 회원의 기수"),
        fieldWithPath("followee[].id").description("내가 팔로우 하는 사람의 ID"),
        fieldWithPath("followee[].name").description("내가 팔로우 하는 사람의 이름"),
        fieldWithPath("followee[].thumbnailPath").description("내가 팔로우 하는 사람의 썸네일 경로"),
        fieldWithPath("followee[].generation").description("내가 팔로우 하는 사람의 기수"),
    };
  }

  ResultActions callUpdateProfileApi(String accessToken, ProfileUpdateRequest request)
      throws Exception {
    return mockMvc.perform(patch("/members/profile")
        .content(asJsonString(request))
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), accessToken))
        .contentType(MediaType.APPLICATION_JSON));
  }

  ;

}
