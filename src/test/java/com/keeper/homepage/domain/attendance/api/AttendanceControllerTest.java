package com.keeper.homepage.domain.attendance.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.listHelper;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.pageHelper;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class AttendanceControllerTest extends IntegrationTest {

  private Member member;
  private String memberToken;
  private final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

  @BeforeEach
  void setUp() {
    member = memberTestHelper.generate();
    memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(), ROLE_회원);

    params.add("page", "0");
    params.add("size", "3");
  }

  @Nested
  @DisplayName("출석 테스트")
  class AttendanceTest {

    @Test
    @DisplayName("유효한 요청일 경우 출석은 성공한다.")
    public void 유효한_요청일_경우_출석은_성공한다() throws Exception {
      String securedValue = getSecuredValue(AttendanceController.class, "create");

      mockMvc.perform(post("/attendances")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)))
          .andExpect(status().isCreated())
          .andDo(document("create-attendance",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              )));
    }
  }

  @Nested
  @DisplayName("출석 랭킹 조회 테스트")
  class GetAttendanceRankTest {

    @BeforeEach
    void setUp() {
      attendanceTestHelper.builder().continuousDay(1).build();
      attendanceTestHelper.builder().continuousDay(2).build();
      attendanceTestHelper.builder().continuousDay(3).build();
    }

    @Test
    @DisplayName("유효한 요청일 경우 당일 출석 랭킹 조회는 성공한다.")
    public void 유효한_요청일_경우_당일_출석_랭킹_조회는_성공한다() throws Exception {
      String securedValue = getSecuredValue(AttendanceController.class, "getTodayRanks");

      mockMvc.perform(get("/attendances/today")
              .params(params)
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)))
          .andDo(document("get-today-attendance-ranks",
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
                  pageHelper(getAttendanceResponse())
              )));
    }

    @Test
    @DisplayName("유효한 요청일 경우 연속 출석 랭킹 조회는 성공한다.")
    public void 유효한_요청일_경우_연속_출석_랭킹_조회는_성공한다() throws Exception {
      String securedValue = getSecuredValue(AttendanceController.class, "getContinuousRanks");

      mockMvc.perform(get("/attendances/continuous")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)))
          .andExpect(status().isOk())
          .andDo(document("get-continuous-attendance-ranks",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              responseFields(
                  listHelper("", getAttendanceResponse())
              )));
    }

    FieldDescriptor[] getAttendanceResponse() {
      return new FieldDescriptor[]{
          fieldWithPath("thumbnailPath").description("회원 썸네일 경로"),
          fieldWithPath("nickName").description("회원 닉네임"),
          fieldWithPath("generation").description("회원 기수"),
          fieldWithPath("continuousDay").description("회원 연속 출석 일수"),
          fieldWithPath("time").description("회원 출석 시간")
      };
    }
  }
}
