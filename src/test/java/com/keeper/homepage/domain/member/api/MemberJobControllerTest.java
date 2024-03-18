package com.keeper.homepage.domain.member.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_부회장;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MemberJobControllerTest extends IntegrationTest {

  private String adminToken;
  private Member member;

  @BeforeEach
  void setUp() {
    member = memberTestHelper.generate();
    member.assignJob(ROLE_회장);
    adminToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(), ROLE_회원, ROLE_회장);
    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("유효한 요청일 경우 임원진 목록 조회는 성공해야 한다.")
  public void 유효한_요청일_경우_임원진_목록_조회는_성공해야_한다() throws Exception {
    String securedValue = getSecuredValue(MemberJobController.class, "getExecutives");

    mockMvc.perform(get("/members/executives")
            .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken)))
        .andExpect(status().isOk())
        .andDo(document("get-executives",
            requestCookies(
                cookieWithName(ACCESS_TOKEN.getTokenName())
                    .description("ACCESS TOKEN %s".formatted(securedValue))
            ),
            responseFields(
                fieldWithPath("[].jobId").description("직책 ID"),
                fieldWithPath("[].jobName").description("직책 이름"),
                fieldWithPath("[].memberId").description("회원 ID"),
                fieldWithPath("[].generation").description("회원 기수"),
                fieldWithPath("[].realName").description("회원 실명")
            )));
  }

  @Test
  @DisplayName("유효한 요청일 경우 임원진 직책 목록 조회는 성공해야 한다.")
  public void 유효한_요청일_경우_임원진_직책_목록_조회는_성공해야_한다() throws Exception {
    String securedValue = getSecuredValue(MemberJobController.class, "getExecutiveJobs");

    mockMvc.perform(get("/members/executive-jobs")
            .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken)))
        .andExpect(status().isOk())
        .andDo(document("get-executive-jobs",
            requestCookies(
                cookieWithName(ACCESS_TOKEN.getTokenName())
                    .description("ACCESS TOKEN %s".formatted(securedValue))
            ),
            responseFields(
                fieldWithPath("[].jobId").description("직책 ID"),
                fieldWithPath("[].jobName").description("직책 이름")
            )));
  }

  @Test
  @DisplayName("유효한 요청일 경우 임원 직책 추가는 성공해야 한다.")
  public void 유효한_요청일_경우_임원_직책_추가는_성공해야_한다() throws Exception {
    String securedValue = getSecuredValue(MemberJobController.class, "addMemberExecutiveJob");

    mockMvc.perform(
            post("/members/{memberId}/executive-jobs/{jobId}", member.getId(), ROLE_부회장.getId())
                .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken)))
        .andExpect(status().isCreated())
        .andDo(document("add-member-executive-job",
            requestCookies(
                cookieWithName(ACCESS_TOKEN.getTokenName())
                    .description("ACCESS TOKEN %s".formatted(securedValue))
            ),
            pathParameters(
                parameterWithName("memberId").description("회원 ID"),
                parameterWithName("jobId").description("임원 직책 ID")
            )));
  }

  @Test
  @DisplayName("유효한 요청일 경우 임원직책 삭제는 성공해야 한다.")
  public void 유효한_요청일_경우_임원_직책_삭제는_성공해야_한다() throws Exception {
    String securedValue = getSecuredValue(MemberJobController.class, "deleteMemberExecutiveJob");

    mockMvc.perform(
            delete("/members/{memberId}/executive-jobs/{jobId}", member.getId(), ROLE_부회장.getId())
                .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken)))
        .andExpect(status().isNoContent())
        .andDo(document("delete-member-executive-job",
            requestCookies(
                cookieWithName(ACCESS_TOKEN.getTokenName())
                    .description("ACCESS TOKEN %s".formatted(securedValue))
            ),
            pathParameters(
                parameterWithName("memberId").description("회원 ID"),
                parameterWithName("jobId").description("임원 직책 ID")
            )));
  }
}
