package com.keeper.homepage.domain.rank.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.listHelper;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.pageHelper;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.domain.member.entity.Member;
import java.io.IOException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class RankControllerTest extends RankApiTestHelper {

  private Member member;
  private String memberToken;
  private final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

  @BeforeEach
  void setUp() {
    member = memberTestHelper.generate();
    memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(), ROLE_회원);
  }

  @Nested
  @DisplayName("누적 포인트 랭킹 테스트")
  class PointRanking {

    @BeforeEach
    void setUp() throws IOException {
      memberTestHelper.builder().point(100).build();
      memberTestHelper.builder().point(1000).build();
    }

    @Test
    @DisplayName("유효한 요청이면 누적 포인트 랭킹 조회는 성공해야 한다.")
    public void 유효한_요청이면_누적_포인트_랭킹_조회는_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(RankController.class, "getPointRanking");

      params.add("page", "0");
      params.add("size", "3");
      callGetPointRankingApi(memberToken, params)
          .andExpect(status().isOk())
          .andDo(document("get-point-ranking",
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

  @Nested
  @DisplayName("개근 랭킹 테스트")
  class ContinuousRanking {

    @BeforeEach
    void setUp() {
      attendanceTestHelper.builder().member(member).continuousDay(1).date(LocalDate.now()).build();
      attendanceTestHelper.builder().member(member).continuousDay(2).date(LocalDate.now().plusDays(1)).build();
      attendanceTestHelper.builder().member(member).continuousDay(3).date(LocalDate.now().plusDays(2)).build();
      attendanceTestHelper.builder().member(member).continuousDay(4).date(LocalDate.now().plusDays(3)).build();
      em.flush();
      em.clear();
    }

    @Test
    @DisplayName("유효한 요청이면 개근 랭킹 조회는 성공해야 한다.")
    public void 유효한_요청이면_개근_랭킹_조회는_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(RankController.class, "getContinuousAttendance");

      callGetContinuousAttendanceRankingApi(memberToken)
          .andExpect(status().isOk())
          .andDo(document("get-continuous-attendance-ranking",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              responseFields(
                  listHelper("list", getContinuousAttendanceResponse())
              )));
    }
  }
}
