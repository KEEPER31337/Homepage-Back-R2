package com.keeper.homepage.domain.member.api;

import static com.keeper.homepage.global.config.security.session.SessionPolicy.SESSION_COOKIE_NAME;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.dto.request.ProfileUpdateRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.MultiValueMap;

public class MemberApiTestHelper extends IntegrationTest {

  ResultActions callGetPointRankingApi(String sessionId, MultiValueMap<String, String> params)
      throws Exception {
    return mockMvc.perform(get("/members/point-rank")
        .params(params)
        .cookie(new Cookie(SESSION_COOKIE_NAME, sessionId)));
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

  FieldDescriptor[] getMyProfileResponse() {
    return new FieldDescriptor[]{
        fieldWithPath("memberId").description("회원의 ID"),
        fieldWithPath("loginId").description("회원의 로그인 ID"),
        fieldWithPath("emailAddress").description("회원의 이메일 주소"),
        fieldWithPath("realName").description("회원의 실명"),
        fieldWithPath("birthday").type(JsonFieldType.STRING).optional()
            .description("회원의 생일 (YYYY-MM-DD, 미등록 시 null)"),
        fieldWithPath("studentId").description("본인의 학번"),
        fieldWithPath("thumbnailPath").type(JsonFieldType.STRING).optional()
            .description("썸네일 경로 (미등록 시 null)"),
        fieldWithPath("generation").description("회원의 기수 (문자열)"),
        fieldWithPath("point").description("회원의 포인트"),
        fieldWithPath("level").description("회원의 레벨"),
        fieldWithPath("totalAttendance").description("회원의 총 출석 횟수"),
        fieldWithPath("memberType").description("회원의 타입"),
        fieldWithPath("memberRank").description("회원의 등급"),
        fieldWithPath("memberJobs").description("회원의 현재 역할 목록 (ROLE_ 접두사 포함)")
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

  ResultActions callUpdateProfileApi(String sessionId, ProfileUpdateRequest request)
      throws Exception {
    return mockMvc.perform(patch("/members/profile")
        .content(asJsonString(request))
        .cookie(new Cookie(SESSION_COOKIE_NAME, sessionId))
        .contentType(MediaType.APPLICATION_JSON));
  }

  ;

}
