package com.keeper.homepage.domain.election.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.domain.election.dto.request.ElectionCreateRequest;
import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.domain.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class AdminTestElectionControllerTest extends AdminElectionApiTestHelper {

  private Member admin;
  private String adminToken;

  @BeforeEach
  void setUp() {
    admin = memberTestHelper.generate();
    adminToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, admin.getId(), ROLE_회장, ROLE_회원);
  }

  @Nested
  @DisplayName("선거 생성 테스트")
  class ElectionCreateTest {

    @Test
    @DisplayName("유효한 요청일 경우 선거 생성은 성공한다.")
    public void 유효한_요청일_경우_선거_생성은_성공한다() throws Exception {
      String securedValue = getSecuredValue(AdminElectionController.class, "createElection");

      ElectionCreateRequest request = ElectionCreateRequest.builder()
          .name("제 1회 임원진 선거")
          .description("임원진 선거입니다.")
          .isAvailable(true)
          .build();

      callCreateElectionApi(adminToken, request)
          .andExpect(status().isCreated())
          .andDo(document("create-election",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("name").description("선거 이름"),
                  fieldWithPath("description").description("선거 설명"),
                  fieldWithPath("isAvailable").description("선거 가능 상태")
              )));
    }
  }

  @Nested
  @DisplayName("선거 삭제 테스트")
  class ElectionDeleteTest {

    @Test
    @DisplayName("유효한 요청일 경우 선거 삭제는 성공한다.")
    public void 유효한_요청일_경우_선거_삭제는_성공한다() throws Exception {
      String securedValue = getSecuredValue(AdminElectionController.class, "deleteElection");

      Election election = electionTestHelper.builder()
          .name("제 1회 임원진 선거")
          .description("임원진 선거입니다.")
          .isAvailable(false)
          .build();

      long electionId = election.getId();

      callDeleteElectionApi(adminToken, electionId)
          .andExpect(status().isNoContent())
          .andDo(document("delete-election",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("electionId")
                      .description("삭제하고자 하는 선거 ID")
              )));
    }
  }
}

