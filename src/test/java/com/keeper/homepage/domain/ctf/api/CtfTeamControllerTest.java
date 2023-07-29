package com.keeper.homepage.domain.ctf.api;

import static com.keeper.homepage.domain.ctf.dto.request.CreateTeamRequest.builder;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.pageHelper;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.ctf.dto.request.CreateTeamRequest;
import com.keeper.homepage.domain.ctf.dto.request.UpdateTeamRequest;
import com.keeper.homepage.domain.ctf.entity.CtfContest;
import com.keeper.homepage.domain.ctf.entity.team.CtfTeam;
import com.keeper.homepage.domain.member.entity.Member;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;

public class CtfTeamControllerTest extends IntegrationTest {

  private Member member;
  private String memberToken;
  private CtfContest ctfContest;

  @BeforeEach
  void setUp() {
    member = memberTestHelper.generate();
    memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(), ROLE_회원);
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

  @Nested
  @DisplayName("CTF 팀 수정 테스트")
  class CtfTeamUpdateTest {

    @Test
    @DisplayName("유효한 요청일 경우 CTF 팀 수정은 성공한다.")
    public void 유효한_요청일_경우_CTF_팀_수정은_성공한다() throws Exception {
      CtfTeam ctfTeam = ctfTeamTestHelper.generate();
      member.join(ctfTeam);

      String securedValue = getSecuredValue(CtfTeamController.class, "updateTeam");

      UpdateTeamRequest request = UpdateTeamRequest.builder()
          .name("KEEPER TEAM")
          .description("2024 CTF TEAM")
          .build();

      mockMvc.perform(put("/ctf/teams/{teamId}", ctfTeam.getId())
              .content(asJsonString(request))
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isCreated())
          .andDo(document("update-ctf-team",
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

  @Nested
  @DisplayName("CTF 팀 조회 테스트")
  class CtfTeamGetTest {

    @Test
    @DisplayName("유효한 요청일 경우 팀 조회는 성공해야 한다.")
    public void 유효한_요청일_경우_팀_조회는_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(CtfTeamController.class, "getTeam");
      CtfTeam ctfTeam = ctfTeamTestHelper.generate();
      member.join(ctfTeam);
      em.flush();
      em.clear();

      mockMvc.perform(get("/ctf/teams/{teamId}", ctfTeam.getId())
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)))
          .andExpect(status().isOk())
          .andDo(document("get-ctf-team",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("teamId").description("조회하고자 하는 팀 ID")
              ),
              responseFields(
                  fieldWithPath("id").description("팀 ID"),
                  fieldWithPath("name").description("팀 이름"),
                  fieldWithPath("description").description("팀 설명"),
                  fieldWithPath("rank").description("팀 현재 순위"),
                  fieldWithPath("score").description("팀 점순"),
                  fieldWithPath("memberNames[]").description("팀원 실명 리스트"),
                  fieldWithPath("solves[]").description("푼 문제 리스트") // TODO: 푼 문제 세부 응답 작성
              )));
    }

    @Test
    @DisplayName("유효한 요청일 경우 팀 목록 조회는 성공해야 한다.")
    public void 유효한_요청일_경우_팀_목록_조회는_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(CtfTeamController.class, "getTeam");
      ctfTeamTestHelper.builder().ctfContest(ctfContest).name("TEAM1").build();
      ctfTeamTestHelper.builder().ctfContest(ctfContest).name("TEAM2").build();
      ctfTeamTestHelper.builder().ctfContest(ctfContest).name("TEAM3").build();
      em.flush();
      em.clear();

      mockMvc.perform(get("/ctf/teams")
              .param("contestId", String.valueOf(ctfContest.getId()))
              .param("search", "")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)))
          .andExpect(status().isOk())
          .andDo(document("get-ctf-teams",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              queryParameters(
                  parameterWithName("contestId").description("CTF 대회 ID"),
                  parameterWithName("search").description("팀 이름 검색어 (안보낼 경우 전체 목록을 조회합니다.)")
              ),
              responseFields(
                  pageHelper(getCtfTeamsResponse())
              )));
    }

    FieldDescriptor[] getCtfTeamsResponse() {
      return new FieldDescriptor[]{
          fieldWithPath("id").description("팀 ID"),
          fieldWithPath("name").description("팀 이름")
      };
    }
  }
}
