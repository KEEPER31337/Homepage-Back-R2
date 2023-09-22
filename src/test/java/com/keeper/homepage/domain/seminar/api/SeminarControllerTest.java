package com.keeper.homepage.domain.seminar.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_서기;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.dateFormat;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.dateTimeFormat;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.field;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.domain.seminar.dto.request.SeminarStartRequest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarIdResponse;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;

public class SeminarControllerTest extends SeminarApiTestHelper {

  private long adminId;
  private long clerkId;
  private long userId;
  private String adminToken;
  private String clerkToken;
  private String userToken;
  private LocalDateTime now;
  private SeminarStartRequest seminarStartRequest;

  @BeforeEach
  void setUp() {
    adminId = memberTestHelper.builder().build().getId();
    clerkId = memberTestHelper.builder().build().getId();
    userId = memberTestHelper.builder().build().getId();
    adminToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, adminId, ROLE_회원, ROLE_회장);
    clerkToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, clerkId, ROLE_회원, ROLE_서기);
    userToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, userId, ROLE_회원);

    now = LocalDateTime.now().withNano(0);
    seminarStartRequest = SeminarStartRequest.builder()
        .attendanceCloseTime(now.plusMinutes(3))
        .latenessCloseTime(now.plusMinutes(4)).build();
  }

  @Nested
  @DisplayName("세미나 생성 테스트")
  class SeminarCreateTest {

    @Test
    @DisplayName("세미나 생성을 성공한다.")
    public void should_successCreateSeminar_when_admin() throws Exception {
      String securedValue = getSecuredValue(SeminarController.class, "createSeminar");
      MvcResult mvcResult = createSeminarUsingApi(adminToken).andExpect(status().isCreated())
          .andDo(document("create-seminar",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName()).description("ACCESS TOKEN %s".formatted(securedValue))),
              queryParameters(
                  parameterWithName("openDate").attributes(dateFormat())
                      .description("세미나 날짜")),
              responseFields(
                  field("id", "세미나 ID"))
          )).andReturn();

      Long seminarId = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
          SeminarIdResponse.class).id();
      Seminar seminar = seminarRepository.findById(seminarId).orElseThrow();

      assertThat(seminar.getAttendanceCloseTime()).isNull();
      assertThat(seminar.getLatenessCloseTime()).isNull();
    }

    @Test
    @DisplayName("관리자 권한이 아니면 세미나 생성을 실패한다.")
    public void should_failCreateSeminar_when_notAdmin() throws Exception {
      createSeminarUsingApi(userToken).andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("세미나 시작 테스트")
  class SeminarStartTest {

    @Test
    @DisplayName("생성된 세미나에 마감 시간을 넣어서 시작한다.")
    public void should_successStartSeminar_when_admin() throws Exception {
      String securedValue = getSecuredValue(SeminarController.class, "startSeminar");
      Long seminarId = createSeminarAndGetId(adminToken);

      startSeminarUsingApi(adminToken, seminarId, seminarStartRequest).andExpect(status().isOk())
          .andDo(document("start-seminar",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName()).description(
                      "ACCESS TOKEN %s".formatted(securedValue))),
              pathParameters(
                  parameterWithName("seminarId")
                      .description("세미나의 ID")
              ),
              requestFields(
                  field("attendanceCloseTime", "출석 마감 시간", dateTimeFormat()),
                  field("latenessCloseTime", "지각 마감 시간", dateTimeFormat())),
              responseFields(
                  field("attendanceCode", "세미나 출석 코드")
              )));

      em.flush();
      em.clear();

      Seminar seminar = seminarRepository.findById(seminarId).orElseThrow();
      assertThat(seminarStartRequest.attendanceCloseTime()).isEqualTo(
          seminar.getAttendanceCloseTime());
      assertThat(seminarStartRequest.latenessCloseTime()).isEqualTo(seminar.getLatenessCloseTime());
    }

    @Test
    @DisplayName("시간 값이 둘 다 null일 때 허용한다. (세미나 생성, 시작이 분리되어 있다.)")
    public void should_success_when_nullValue() throws Exception {
      String strJson = """
          {"attendanceCloseTime":null, "latenessCloseTime":null}""";
      Long seminarId = createSeminarAndGetId(adminToken);

      startSeminarUsingApi(adminToken, seminarId, strJson).andExpect(status().isOk());
      startSeminarUsingApi(adminToken, seminarId, SeminarStartRequest.builder().build())
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("올바르지 않은 시간 값이 들어오면 세미나 시작을 실패한다.")
    public void should_failStartSeminar_when_NotValidValue() throws Exception {
      DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      String strJson1 = """
          {"attendanceCloseTime":"null", "latenessCloseTime":"null"}""";
      String strJson2 = """
          {"attendanceCloseTime":"null", "latenessCloseTime":null}""";
      String strJson3 = """
          {"attendanceCloseTime":null, "latenessCloseTime":"null"}""";
      String strJson4 = """
          {"attendanceCloseTime":"%s", "latenessCloseTime":null}
          """.formatted(now.plusMinutes(3).format(format));
      String strJson5 = """
          {"attendanceCloseTime":null, "latenessCloseTime":"%s"}
          """.formatted(now.plusMinutes(3).format(format));

      createSeminarUsingApi(adminToken).andExpect(status().isCreated());
      Long seminarId = seminarService.findAll().seminarList().stream().findAny().orElseThrow()
          .getId();
      startSeminarUsingApi(adminToken, seminarId, strJson1).andExpect(status().isBadRequest());
      startSeminarUsingApi(adminToken, seminarId, strJson2).andExpect(status().isBadRequest());
      startSeminarUsingApi(adminToken, seminarId, strJson3).andExpect(status().isBadRequest());
      startSeminarUsingApi(adminToken, seminarId, strJson4).andExpect(status().isBadRequest());
      startSeminarUsingApi(adminToken, seminarId, strJson5).andExpect(status().isBadRequest());
      startSeminarUsingApi(adminToken, seminarId, "asdf").andExpect(status().isBadRequest());
      startSeminarUsingApi(adminToken, seminarId, (SeminarStartRequest) null)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("세미나 출석 마감 시간 또는 지각 마감 시간이 과거인 경우 등록을 실패한다.")
    public void should_failCreateSeminar_when_closeTimeIsPast() throws Exception {
      seminarStartRequest = SeminarStartRequest.builder()
          .attendanceCloseTime(now.plusMinutes(-5))
          .latenessCloseTime(now.plusMinutes(-3)).build();

      createSeminarUsingApi(adminToken).andExpect(status().isCreated());
      Long seminarId = seminarService.findAll().seminarList().stream().findAny().orElseThrow()
          .getId();
      startSeminarUsingApi(adminToken, seminarId, seminarStartRequest)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("세미나 출석 마감 시간이 지각 마감 시간보다 미래인 경우 등록을 실패한다.")
    public void should_failCreateSeminar_when_attendanceTimeGreaterThanCloseTime()
        throws Exception {
      seminarStartRequest = SeminarStartRequest.builder()
          .attendanceCloseTime(now.plusMinutes(5))
          .latenessCloseTime(now.plusMinutes(3)).build();

      createSeminarUsingApi(adminToken).andExpect(status().isCreated());
      Long seminarId = seminarService.findAll().seminarList().stream().findAny().orElseThrow()
          .getId();
      startSeminarUsingApi(adminToken, seminarId, seminarStartRequest)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("관리자 권한이 아니면 세미나 시작을 실패한다.")
    public void should_failCreateSeminar_when_notAdmin() throws Exception {
      createSeminarUsingApi(adminToken).andExpect(status().isCreated());
      Long seminarId = seminarService.findAll().seminarList().stream().findAny().orElseThrow()
          .getId();
      startSeminarUsingApi(userToken, seminarId, seminarStartRequest)
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("세미나 조회 테스트")
  class SeminarCheckTest {

    @Test
    @DisplayName("생성된 세미나의 개수 및 데이터를 확인한다.")
    public void should_checkCountSeminar_when_admin() throws Exception {
      String securedValue = getSecuredValue(SeminarController.class, "getAllSeminars");

      int beforeLength = validSeminarFindService.findAll().size();
      Long seminarId = createSeminarAndGetId(adminToken);
      startSeminarUsingApi(adminToken, seminarId, seminarStartRequest).andExpect(status().isOk());
      em.flush();
      em.clear();

      int afterLength = validSeminarFindService.findAll().size();
      assertThat(afterLength).isEqualTo(beforeLength + 1);

      var responseSeminarListDescriptors = new FieldDescriptor[]{
          field("seminarList[].id", "세미나 ID"),
          field("seminarList[].openTime", "세미나 생성 시간"),
          field("seminarList[].attendanceCloseTime", "출석 마감 시간"),
          field("seminarList[].latenessCloseTime", "지각 마감 시간"),
          field("seminarList[].attendanceCode", "출석 코드"),
          field("seminarList[].name", "세미나명"),
          field("seminarList[].registerTime", "DB 생성 시간"),
          field("seminarList[].updateTime", "DB 업데이트 시간")
      };

      int idx = afterLength - 1;
      searchAllSeminarUsingApi(adminToken)
          .andExpect(jsonPath("$.seminarList.length()", is(afterLength)))
          .andExpect(combineJsonPath("openTime", idx).exists())
          .andExpect(combineJsonPath("attendanceCloseTime", idx).exists())
          .andExpect(combineJsonPath("latenessCloseTime", idx).exists())
          .andExpect(combineJsonPath("attendanceCode", idx).exists())
          .andExpect(combineJsonPath("name", idx).exists())
          .andExpect(combineJsonPath("registerTime", idx).exists())
          .andExpect(combineJsonPath("updateTime", idx).exists())

          .andDo(document("search-all-seminar",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))),
              responseFields(
                  responseSeminarListDescriptors)
          ));
    }

    public JsonPathResultMatchers combineJsonPath(String name, int idx) {
      String parseFormat = "$.seminarList[%d].%s";
      return jsonPath(parseFormat, idx, name);
    }

    @Test
    @DisplayName("관리자 권한이 아니면 세미나 조회를 실패한다.")
    public void should_failSearchSeminar_when_notAdmin() throws Exception {
      createSeminarUsingApi(adminToken).andExpect(status().isCreated());
      searchAllSeminarUsingApi(userToken).andExpect(status().isForbidden());
    }

    @Nested
    @DisplayName("세미나 ID 조회 테스트")
    class SeminarCheckIdTest {

      @Test
      @DisplayName("id 값으로 세미나 조회를 성공한다.")
      public void should_successSearchSeminarUsingId_when_admin() throws Exception {
        String securedValue = getSecuredValue(SeminarController.class, "getSeminar");

        Long seminarId = createSeminarAndGetId(adminToken);
        startSeminarUsingApi(adminToken, seminarId, seminarStartRequest).andExpect(status().isOk());
        em.flush();
        em.clear();

        var responseSeminarDescriptors = new FieldDescriptor[]{
            field("id", "세미나 ID"),
            field("openTime", "세미나 생성 시간"),
            field("attendanceCloseTime", "출석 마감 시간").optional(),
            field("latenessCloseTime", "지각 마감 시간").optional(),
            field("name", "세미나명"),
            field("statusType", "세미나 출석 상태"),
            field("attendanceCode", "세미나 출석 코드").optional(),
            field("starterId", "세미나 출석을 시작한 회원 ID").optional()
        };

        searchSeminarUsingApi(adminToken, seminarId).andExpect(status().isOk())
            .andDo(document("search-seminar",
                requestCookies(
                    cookieWithName(ACCESS_TOKEN.getTokenName())
                        .description("ACCESS TOKEN %s".formatted(securedValue))),
                pathParameters(
                    parameterWithName("seminarId").description("검색할 세미나 ID를 입력해주세요.")),
                responseFields(
                    responseSeminarDescriptors)
            ));
      }

      @Test
      @DisplayName("존재하지 않는 세미나를 조회했을 때 실패한다.")
      public void should_failSearchSeminarNotExistId_when_admin() throws Exception {
        searchSeminarUsingApi(adminToken, 0L).andExpect(status().isNotFound());
      }
    }

    @Nested
    @DisplayName("이용 가능한 세미나 조회 테스트")
    class SeminarCheckAvailableTest {

      @Test
      @DisplayName("이용 가능한 세미나를 조회한다.")
      public void should_search_when_availableSeminar() throws Exception {
        String securedValue = getSecuredValue(SeminarController.class, "availableSeminar");

        Long seminarId = createSeminarAndGetId(adminToken);
        startSeminarUsingApi(adminToken, seminarId, seminarStartRequest).andExpect(status().isOk());
        em.flush();
        em.clear();

        var responseSeminarDescriptors = new FieldDescriptor[]{
            field("id", "세미나 ID"),
            field("openTime", "세미나 생성 시간"),
            field("attendanceCloseTime", "출석 마감 시간"),
            field("latenessCloseTime", "지각 마감 시간"),
            field("attendanceCode", "출석 코드"),
            field("name", "세미나명"),
            field("registerTime", "DB 생성 시간"),
            field("updateTime", "DB 업데이트 시간")
        };

        searchAvailableSeminarUsingApi(adminToken).andExpect(status().isOk())
            .andDo(document("search-available-seminar",
                requestCookies(
                    cookieWithName(ACCESS_TOKEN.getTokenName())
                        .description("ACCESS TOKEN %s".formatted(securedValue))),
                responseFields(
                    responseSeminarDescriptors)
            ));
      }

      @Test
      @DisplayName("현재 시간이 지각 마감 시간을 지난 경우 조회 가능한 세미나가 존재하지 않는다.")
      public void should_notSearch_when_timeOutSeminar() throws Exception {
        Seminar seminar = seminarTestHelper.builder()
            .attendanceCloseTime(now.plusMinutes(-2))
            .latenessCloseTime(now.plusMinutes(-1))
            .build();
        em.clear();

        searchAvailableSeminarUsingApi(adminToken).andExpect(status().isOk())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.openTime").isEmpty())
            .andExpect(jsonPath("$.attendanceCloseTime").isEmpty())
            .andExpect(jsonPath("$.latenessCloseTime").isEmpty())
            .andExpect(jsonPath("$.attendanceCode").isEmpty())
            .andExpect(jsonPath("$.name").isEmpty())
            .andExpect(jsonPath("$.registerTime").isEmpty())
            .andExpect(jsonPath("$.updateTime").isEmpty());
      }

      @Test
      @DisplayName("모든 권한이 이용 가능한 세미나를 조회할 수 있다.")
      public void should_successSearchSeminar_when_AllMember() throws Exception {
        Long seminarId = createSeminarAndGetId(adminToken);
        startSeminarUsingApi(adminToken, seminarId, seminarStartRequest).andExpect(status().isOk());
        em.flush();
        em.clear();

        searchAvailableSeminarUsingApi(adminToken).andExpect(status().isOk());
        searchAvailableSeminarUsingApi(userToken).andExpect(status().isOk());
        searchAvailableSeminarUsingApi(clerkToken).andExpect(status().isOk());
      }
    }

    @Nested
    @DisplayName("세미나 날짜 조회 테스트")
    class SeminarCheckDateTest {

      @Test
      @DisplayName("세미나를 날짜로 필터링하여 조회한다.")
      public void should_searchSeminar_when_filterDate() throws Exception {
        String securedValue = getSecuredValue(SeminarController.class, "getSeminarByDate");

        Long seminarId = createSeminarAndGetId(adminToken);
        startSeminarUsingApi(adminToken, seminarId, seminarStartRequest).andExpect(status().isOk());
        em.flush();
        em.clear();

        var responseSeminarDescriptors = new FieldDescriptor[]{
            field("id", "세미나 ID"),
            field("openTime", "세미나 생성 시간"),
            field("attendanceCloseTime", "출석 마감 시간"),
            field("latenessCloseTime", "지각 마감 시간"),
            field("attendanceCode", "출석 코드"),
            field("name", "세미나명"),
            field("registerTime", "DB 생성 시간"),
            field("updateTime", "DB 업데이트 시간")
        };

        searchDateSeminarUsingApi(adminToken, LocalDate.now().toString())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.openTime").exists())
            .andExpect(jsonPath("$.attendanceCloseTime").exists())
            .andExpect(jsonPath("$.latenessCloseTime").exists())
            .andExpect(jsonPath("$.attendanceCode").exists())
            .andExpect(jsonPath("$.name").exists())
            .andExpect(jsonPath("$.registerTime").exists())
            .andExpect(jsonPath("$.updateTime").exists())

            .andDo(document("search-date-seminar",
                requestCookies(
                    cookieWithName(ACCESS_TOKEN.getTokenName())
                        .description("ACCESS TOKEN %s".formatted(securedValue))),
                queryParameters(
                    parameterWithName("date").attributes(dateFormat())
                        .description("검색할 날짜를 입력해주세요.")),
                responseFields(
                    responseSeminarDescriptors)
            ));
      }

      @Test
      @DisplayName("서기는 날짜로 세미나를 조회할 수 없다.")
      public void should_badRequest_when_clerkSearchDate() throws Exception {
        searchDateSeminarUsingApi(clerkToken, LocalDate.now().toString())
            .andExpect(status().isForbidden());
      }

      @Test
      @DisplayName("date 값의 형식은 맞지만 데이터가 없을 때 200 (OK)을 반환한다.")
      public void should_OK_when_validDate() throws Exception {
        searchDateSeminarUsingApi(adminToken, "2022-02-02")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.openTime").isEmpty())
            .andExpect(jsonPath("$.attendanceCloseTime").isEmpty())
            .andExpect(jsonPath("$.latenessCloseTime").isEmpty())
            .andExpect(jsonPath("$.attendanceCode").isEmpty())
            .andExpect(jsonPath("$.name").isEmpty())
            .andExpect(jsonPath("$.registerTime").isEmpty())
            .andExpect(jsonPath("$.updateTime").isEmpty());
      }

      @Test
      @DisplayName("date 값의 형식이 맞지 않으면 400 (Bad Request)을 반환한다.")
      public void should_badRequest_when_invalidDate() throws Exception {
        searchDateSeminarUsingApi(adminToken, "20220202").andExpect(status().isBadRequest());
        searchDateSeminarUsingApi(adminToken, null).andExpect(status().isBadRequest());
      }

      @Test
      @DisplayName("관리자 권한이 아니면 날짜로 필터링하여 조회했을 때 실패한다.")
      public void should_failFilterDateSearchSeminar_when_notAdmin() throws Exception {
        searchDateSeminarUsingApi(userToken, LocalDate.now().toString())
            .andExpect(status().isForbidden());
      }
    }
  }

  @Nested
  @DisplayName("세미나 삭제 테스트")
  class SeminarDeleteTest {

    @Test
    @DisplayName("세미나 삭제를 성공한다.")
    public void should_successDeleteSeminar_when_admin() throws Exception {
      String securedValue = getSecuredValue(SeminarController.class, "deleteSeminar");

      Long seminarId = createSeminarAndGetId(adminToken);
      startSeminarUsingApi(adminToken, seminarId, seminarStartRequest).andExpect(status().isOk());
      em.flush();
      em.clear();

      int beforeLength = validSeminarFindService.findAll().size();
      deleteSeminarUsingApi(adminToken, seminarId).andExpect(status().isNoContent())
          .andDo(document("delete-seminar",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))),
              pathParameters(
                  parameterWithName("seminarId").description("삭제할 세미나 ID를 입력해주세요."))
          ));

      int afterLength = validSeminarFindService.findAll().size();
      assertThat(afterLength).isEqualTo(beforeLength - 1);
      searchAllSeminarUsingApi(adminToken)
          .andExpect(jsonPath("$.seminarList.length()", is(afterLength)));
    }

    @Test
    @DisplayName("관리자 권한이 아니면 세미나 삭제를 실패한다.")
    public void should_failDeleteSeminar_when_notAdmin() throws Exception {
      deleteSeminarUsingApi(userToken, 2L).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("존재하지 않는 세미나를 삭제했을 때 실패한다.")
    public void should_failDeleteNotExistsSeminar_when_admin() throws Exception {
      deleteSeminarUsingApi(adminToken, -1L).andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("가장 최근에 마감된 세미나 조회 테스트")
  class GetRecentlyDoneSeminarTest {

    @Test
    @DisplayName("가장 최근에 마감된 세미나는 성공적으로 조회된다.")
    public void 가장_최근에_마감된_세미나는_성공적으로_조회된다() throws Exception {
      String securedValue = getSecuredValue(SeminarController.class, "getRecentlyDoneSeminar");

      seminarTestHelper.builder()
          .openTime(LocalDateTime.now().minusDays(1))
          .latenessCloseTime(LocalDateTime.now().minusDays(1))
          .build();

      em.flush();
      em.clear();

      mockMvc.perform(get("/seminars/recently-done")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), userToken)))
          .andExpect(status().isOk())
          .andDo(document("get-recently-done-seminar",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              responseFields(field("id", "세미나 ID"))
          ));
    }
  }

  @Nested
  @DisplayName("가장 최근의 예정된 세미나 조회 테스트")
  class GetRecentlyUpcomingSeminarTest {

    @Test
    @DisplayName("유효한 요청일 경우 가장 최근의 예정된 세미나는 성공적으로 조회된다.")
    public void 유효한_요청일_경우_가장_최근의_예정된_세미나는_성공적으로_조회된다() throws Exception {
      String securedValue = getSecuredValue(SeminarController.class, "getRecentlyUpcomingSeminars");

      seminarTestHelper.builder()
          .openTime(LocalDateTime.now())
          .build();
      seminarTestHelper.builder()
          .openTime(LocalDateTime.now().plusDays(1))
          .build();
      em.flush();
      em.clear();

      mockMvc.perform(get("/seminars/recently-upcoming")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), userToken)))
          .andExpect(status().isOk())
          .andDo(document("get-recently-upcoming-seminars",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              responseFields(field("[].id", "세미나 ID"))
          ));
    }
  }
}
