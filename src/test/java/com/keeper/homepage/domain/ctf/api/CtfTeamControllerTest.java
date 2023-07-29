package com.keeper.homepage.domain.ctf.api;

import static com.keeper.homepage.domain.ctf.dto.request.CreateTeamRequest.builder;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
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
import com.keeper.homepage.domain.ctf.dto.request.CreateTeamRequest;
import com.keeper.homepage.domain.ctf.entity.CtfContest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class CtfTeamControllerTest extends IntegrationTest {

  private String memberToken;
  private CtfContest ctfContest;

  @BeforeEach
  void setUp() {
    long memberId = memberTestHelper.generate().getId();
    memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, memberId, ROLE_회원);
    ctfContest = ctfContestTestHelper.generate();
  }

  @Nested
  @DisplayName("CTF 팀 생성 테스트")
  class CtfTeamCreateTest {

    @Test
    @DisplayName("유효한 요청이면 CTF 팀 생성은 가능해야 한다.")
    public void 유효한_요청이면_CTF_팀_생성은_가능해야_한다() throws Exception {
      String securedValue = getSecuredValue(CtfTeamController.class, "createTeam");

      CreateTeamRequest request = builder()
          .name("KEEPER TEAM")
          .description("2024 CTF TEAM")
          .contestId(ctfContest.getId())
          .build();

      mockMvc.perform(post("/ctf/teams")
              .content(asJsonString(request))
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isCreated())
          .andDo(document("create-ctf-team",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("name").description("CTF 팀명"),
                  fieldWithPath("description").description("CTF 팀 설명"),
                  fieldWithPath("contestId").description("CTF 대회 ID")
              )));
    }
  }
}
