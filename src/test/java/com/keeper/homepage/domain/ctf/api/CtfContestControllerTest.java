package com.keeper.homepage.domain.ctf.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.ctf.dto.request.contest.CreateContestRequest;
import com.keeper.homepage.domain.member.entity.Member;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class CtfContestControllerTest extends IntegrationTest {

  private Member admin;
  private String adminToken;

  @BeforeEach
  void setUp() {
    admin = memberTestHelper.generate();
    adminToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, admin.getId(), ROLE_회원, ROLE_회장);
  }

  @Nested
  @DisplayName("CTF 대회 생성 테스트")
  class CtfContestCreateTest {

    @Test
    @DisplayName("유효한 요청일 경우 CTF 대회 생성은 성공한다.")
    public void 유효한_요청일_경우_CTF_대회_생성은_성공한다() throws Exception {
      String securedValue = getSecuredValue(CtfContestController.class, "createContest");

      CreateContestRequest request = CreateContestRequest.builder()
          .name("2024 KEEPER CTF")
          .description("2024 KEEPER CTF 입니다.")
          .build();

      mockMvc.perform(post("/ctf/contests")
              .content(asJsonString(request))
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isCreated())
          .andDo(document("create-ctf-contest",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("name").description("CTF 팀명"),
                  fieldWithPath("description").description("CTF 팀 설명")
              )));
    }
  }
}
