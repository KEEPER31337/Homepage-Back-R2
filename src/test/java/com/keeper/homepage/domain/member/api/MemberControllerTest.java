package com.keeper.homepage.domain.member.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.config.security.data.JwtType.REFRESH_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.pageHelper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.dto.request.ChangePasswordRequest;
import com.keeper.homepage.domain.member.entity.Member;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.snippet.Attributes.Attribute;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class MemberControllerTest extends MemberApiTestHelper {

  @Nested
  class ChangePassword {

    private Member member;
    private Cookie[] tokenCookies;

    @BeforeEach
    void setup() {
      member = memberTestHelper.generate();
      tokenCookies = memberTestHelper.getTokenCookies(member);
    }

    @Test
    @DisplayName("유효한 비밀번호로 변경을 요청했을 때 204 NO CONTENT를 반환해야 한다.")
    void should_responseNoContent_when_validPassword() throws Exception {
      ChangePasswordRequest request = ChangePasswordRequest.from("password123!@#");

      mockMvc.perform(patch("/members/change-password")
              .cookie(tokenCookies)
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andExpect(status().isNoContent())
          .andExpect(header().string(HttpHeaders.LOCATION, "/members/me"))
          .andDo(document("change-password",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName()).description("ACCESS TOKEN"),
                  cookieWithName(REFRESH_TOKEN.getTokenName()).description("REFRESH TOKEN")),
              requestFields(
                  fieldWithPath("newPassword").description("새로운 패스워드")),
              responseHeaders(
                  headerWithName(HttpHeaders.LOCATION).description("비밀번호 변경한 리소스의 위치입니다."))));
    }
  }

  @Nested
  @DisplayName("회원 목록 조회")
  class GetMembers {

    private Member member;
    private String memberToken;

    @BeforeEach
    void setUp() throws IOException {
      member = memberTestHelper.builder().build();
      memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(), ROLE_회원);
    }

    @Test
    @DisplayName("유효한 요청일 경우 회원 목록 조회(검색)는 성공한다.")
    public void 유효한_요청일_경우_회원_목록_조회는_성공한다() throws Exception {
      assertThat(member.getGeneration()).isNotNull();
      String securedValue = getSecuredValue(MemberController.class, "getMembersByRealName");

      mockMvc.perform(get("/members/real-name")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andDo(document("get-members-by-real-name",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              queryParameters(
                  parameterWithName("searchName").description("회원 이름 검색어")
                      .attributes(new Attribute("format", "null : 전체 목록 조회"))
                      .optional()
              ),
              responseFields(
                  fieldWithPath("[].memberId").description("회원 ID"),
                  fieldWithPath("[].memberName").description("회원 실명"),
                  fieldWithPath("[].generation").description("회원 기수")
              )));
    }
  }

  @Nested
  @DisplayName("누적 포인트 랭킹 테스트")
  class PointRanking {

    private String memberToken;
    private final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    @BeforeEach
    void setUp() throws IOException {
      long memberId;
      memberTestHelper.builder().point(0).build();
      memberTestHelper.builder().point(100).build();
      memberId = memberTestHelper.builder().point(1000).build().getId();
      memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, memberId, ROLE_회원);
    }

    @Test
    @DisplayName("유효한 요청이면 누적 포인트 랭킹 조회는 성공해야 한다.")
    public void 유효한_요청이면_누적_포인트_랭킹_조회는_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(MemberController.class, "getPointRanks");

      params.add("page", "0");
      params.add("size", "3");
      callGetPointRankingApi(memberToken, params)
          .andExpect(status().isOk())
          .andDo(document("get-point-ranks",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              queryParameters(
                  parameterWithName("page").description("페이지 (default: 0)")
                      .optional(),
                  parameterWithName("size").description("한 페이지당 불러올 개수 (default: 10)")
                      .optional()
              ),
              responseFields(
                  pageHelper(getPointRankResponse())
              )));
    }
  }
}
