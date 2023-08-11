package com.keeper.homepage.domain.merit.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.pageHelper;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType;
import com.keeper.homepage.domain.merit.dto.request.AddMeritTypeRequest;
import com.keeper.homepage.domain.merit.dto.request.GiveMeritPointRequest;
import com.keeper.homepage.domain.merit.dto.request.UpdateMeritTypeRequest;
import com.keeper.homepage.domain.merit.entity.MeritType;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultActions;


public class MeritControllerTest extends MeritApiTestHelper {


  private MeritType meritType;
  private Member member, other;
  private String memberToken, otherToken;

  @BeforeEach
  void setUp() throws IOException {
    meritType = meritTypeHelper.builder().merit(3).detail("우수기술문서작성").build();
    member = memberTestHelper.generate();
    other = memberTestHelper.generate();
    memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(),
        MemberJobType.ROLE_회원);
  }


  @Nested
  @DisplayName("상벌점 테스트")
  @Component
  class ControllerTest {


    @Test
    @DisplayName("상벌점 목록 조회를 성공해야 한다.")
    void 상벌점_목록_조회를_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(MeritController.class, "searchMeritLogList");

      meritLogTestHelper.generate();
      meritLogTestHelper.generate();
      meritLogTestHelper.generate();

      ResultActions perform = mockMvc.perform(get("/merits")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
          .andDo(document("search-meritLog",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              responseFields(
                  pageHelper(getMeritLogResponse())
              )));
    }

    @Test
    @DisplayName("회원별 상벌점 목록 조회를 성공해야 한다.")
    void 회원별_상벌점_목록_조회를_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(MeritController.class, "findMeritLogByMemberId");

      meritLogTestHelper.builder().giver(member).build();
      meritLogTestHelper.builder().giver(member).build();
      meritLogTestHelper.builder().giver(member).build();
      meritLogTestHelper.builder().giver(other).build();
      meritLogTestHelper.builder().giver(other).build();

      ResultActions resultActions = mockMvc.perform(
              get("/merits/members/{memberId}", member.getId())
                  .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
          .andDo(document("search-member-meritLog",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("memberId").description("조회하고자 하는 멤버의 ID 값")
              ),
              responseFields(
                  pageHelper(getAwarderMeritLogResponse())
              )));
    }

    @Test
    @DisplayName("상벌점 타입 조회를 성공해야 한다.")
    void 상벌점_조회는_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(MeritController.class, "searchMeritType");

      ResultActions perform = mockMvc.perform(get("/merits/types")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
          .andDo(document("search-meritType",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              responseFields(
                  pageHelper(getMeritTypeResponse())
              )));
    }
  }

  @Nested
  @DisplayName("상벌점 타입 생성 테스트")
  class MeritTypeTest {

    @Test
    @DisplayName("상벌점 타입 생성을 성공해야 한다.")
    void 상벌점_타입_생성을_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(MeritController.class, "registerMeritType");

      AddMeritTypeRequest request = AddMeritTypeRequest.builder()
          .score(3)
          .reason("우수기술문서 작성")
          .build();

      mockMvc.perform(post("/merits/types")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken))
              .content(asJsonString(request))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isCreated())
          .andDo(document("create-meritType",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("score").description("상벌점 점수를 입력해주세요."),
                  fieldWithPath("reason").description("상벌점 사유를 입력해주세요.")
              )));
    }

  }

  @Nested
  @DisplayName("상벌점 부여 로그 생성 테스트")
  class MeritLogTest {

    @Test
    @DisplayName("상벌점 부여 로그 생성을 성공해야 한다.")
    void 상벌점_부여_로그_생성을_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(MeritController.class, "registerMerit");

      GiveMeritPointRequest request = GiveMeritPointRequest.builder()
          .awarderId(member.getId())
          .meritTypeId(meritType.getId())
          .build();

      MockPart mockPart = new MockPart("request", asJsonString(request).getBytes(
          StandardCharsets.UTF_8));
      mockPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

      mockMvc.perform(multipart("/merits")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken))
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andExpect(status().isCreated())
          .andDo(document("create-meritLog",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("awarderId").description("수여자의 ID"),
                  fieldWithPath("meritTypeId").description("상벌점 타입의 ID")
              )));
    }
  }

  @Nested
  @DisplayName("상벌점 부여 로그 수정 테스트")
  class MeritLogUpdateTest {

    @Test
    @DisplayName("상벌점 부여 로그 수정을 성공해야 한다.")
    void 상벌점_부여_로그_수정을_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(MeritController.class, "updateMeritType");

      UpdateMeritTypeRequest request = UpdateMeritTypeRequest.builder()
          .score(5)
          .reason("거짓 스터디")
          .build();

      mockMvc.perform(put("/merits/types/{meritTypeId}", meritType.getId())
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken))
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andExpect(status().isCreated())
          .andDo(document("update-meritType",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("score").description("수정할 점수"),
                  fieldWithPath("reason").description("수정할 사유")
              )));
    }
  }
}
