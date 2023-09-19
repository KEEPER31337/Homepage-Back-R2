package com.keeper.homepage.domain.seminar.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.ABSENCE;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.ATTENDANCE;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.LATENESS;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.field;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.pageHelper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.seminar.dto.request.SeminarAttendanceCodeRequest;
import com.keeper.homepage.domain.seminar.dto.request.SeminarAttendanceStatusRequest;
import com.keeper.homepage.domain.seminar.dto.request.SeminarStartRequest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarAttendanceResponse;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MvcResult;

public class SeminarAttendanceControllerTest extends SeminarApiTestHelper {

  private long adminId;
  private long userId;
  private String adminToken;
  private String userToken;
  private LocalDateTime now;
  private SeminarStartRequest seminarStartRequest;

  @BeforeEach
  void setUp() {
    adminId = memberTestHelper.builder().build().getId();
    userId = memberTestHelper.builder().build().getId();
    adminToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, adminId, ROLE_회원, ROLE_회장);
    userToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, userId, ROLE_회원);

    now = LocalDateTime.now().withNano(0);
    seminarStartRequest = SeminarStartRequest.builder()
        .attendanceCloseTime(now.plusMinutes(3))
        .latenessCloseTime(now.plusMinutes(4)).build();
  }

  @Nested
  @DisplayName("세미나 출석 테스트")
  class SeminarAttendanceTest {

    @Test
    @DisplayName("세미나 출석을 성공한다.")
    public void should_success_attendanceSeminar() throws Exception {
      String securedValue = getSecuredValue(SeminarAttendanceController.class, "attendanceSeminar");

      Long seminarId = createSeminarAndGetId(adminToken);
      String attendanceCode = seminarRepository.findById(seminarId).orElseThrow()
          .getAttendanceCode();

      SeminarAttendanceCodeRequest request = SeminarAttendanceCodeRequest.builder()
          .attendanceCode(attendanceCode)
          .build();

      startSeminarUsingApi(adminToken, seminarId, seminarStartRequest).andExpect(status().isOk());

      MvcResult mvcResult = attendanceSeminarUsingApi(userToken, seminarId, request)
          .andExpect(status().isCreated())
          .andDo(document("attendance-seminar",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName()).description(
                      "ACCESS TOKEN %s".formatted(securedValue))),
              pathParameters(
                  parameterWithName("seminarId")
                      .description("세미나의 ID")
              ),
              requestFields(
                  field("attendanceCode", "세미나 출석 코드")),
              responseFields(
                  field("id", "세미나 출석 ID"),
                  field("statusType", "출석 상태"))
          )).andReturn();

      SeminarAttendanceStatusType statusType = objectMapper.readValue(
          mvcResult.getResponse().getContentAsString(),
          SeminarAttendanceResponse.class).getStatusType();

      assertThat(statusType).isEqualTo(ATTENDANCE);
    }

    @Test
    @DisplayName("세미나 중복 출석을 실패한다.")
    public void should_fail_attendanceSeminarDuplicate() throws Exception {
      Long seminarId = createSeminarAndGetId(adminToken);
      String attendanceCode = seminarRepository.findById(seminarId).orElseThrow()
          .getAttendanceCode();

      SeminarAttendanceCodeRequest request = SeminarAttendanceCodeRequest.builder()
          .attendanceCode(attendanceCode)
          .build();

      startSeminarUsingApi(adminToken, seminarId, seminarStartRequest).andExpect(status().isOk());
      attendanceSeminarUsingApi(userToken, seminarId, request).andExpect(status().isCreated());
      em.flush();
      em.clear();

      attendanceSeminarUsingApi(userToken, seminarId, request).andExpect(status().isConflict());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"\"\""})
    @DisplayName("비정상적인 입력을 시도했을 때 실패한다.")
    public void should_fail_invalidInputValue(String attendanceCode) throws Exception {
      String strJson = """
          {"attendanceCode":%s}""";
      Long seminarId = createSeminarAndGetId(adminToken);

      startSeminarUsingApi(adminToken, seminarId, seminarStartRequest).andExpect(status().isOk());
      attendanceSeminarUsingApi(adminToken, seminarId, strJson.formatted(attendanceCode)).andExpect(
          status().isBadRequest());
    }

    @Test
    @DisplayName("세미나 지각을 성공한다.")
    public void should_success_when_attendanceSeminarLateness() throws Exception {
      long seminarId = seminarService.save(LocalDate.now()).id();
      Seminar seminar = seminarRepository.findById(seminarId).orElseThrow();
      seminar.changeCloseTime(now.plusMinutes(-2), now.plusMinutes(3));

      String attendanceCode = seminarRepository.findById(seminarId).orElseThrow()
          .getAttendanceCode();

      SeminarAttendanceCodeRequest request = SeminarAttendanceCodeRequest.builder()
          .attendanceCode(attendanceCode)
          .build();

      MvcResult mvcResult = attendanceSeminarUsingApi(adminToken, seminarId, request)
          .andExpect(status().isCreated()).andReturn();

      SeminarAttendanceStatusType statusType = objectMapper.readValue(
          mvcResult.getResponse().getContentAsString(),
          SeminarAttendanceResponse.class).getStatusType();

      assertThat(statusType).isEqualTo(LATENESS);
    }

    @Test
    @DisplayName("세미나 지각 마감 시간이 지나면 결석 처리한다.")
    public void should_success_when_attendanceSeminarAbsence() throws Exception {
      long seminarId = seminarService.save(LocalDate.now()).id();
      Seminar seminar = seminarRepository.findById(seminarId).orElseThrow();
      seminar.changeCloseTime(now.plusMinutes(-2), now.plusMinutes(-1));

      String attendanceCode = seminarRepository.findById(seminarId).orElseThrow()
          .getAttendanceCode();

      SeminarAttendanceCodeRequest request = SeminarAttendanceCodeRequest.builder()
          .attendanceCode(attendanceCode)
          .build();

      MvcResult mvcResult = attendanceSeminarUsingApi(adminToken, seminarId, request)
          .andExpect(status().isCreated()).andReturn();

      SeminarAttendanceStatusType statusType = objectMapper.readValue(
          mvcResult.getResponse().getContentAsString(),
          SeminarAttendanceResponse.class).getStatusType();

      assertThat(statusType).isEqualTo(ABSENCE);
    }
  }

  @Nested
  @DisplayName("세미나 출석 상태 변경 테스트")
  class SeminarAttendanceStatusTest {

    @Test
    @DisplayName("세미나 출석 상태를 변경한다.")
    public void should_success_when_changeSeminarAttendanceStatus() throws Exception {
      String securedValue = getSecuredValue(SeminarAttendanceController.class, "changeAttendanceStatus");

      Long seminarId = createSeminarAndGetId(adminToken);
      String attendanceCode = seminarRepository.findById(seminarId).orElseThrow()
          .getAttendanceCode();

      SeminarAttendanceCodeRequest request = SeminarAttendanceCodeRequest.builder()
          .attendanceCode(attendanceCode)
          .build();

      SeminarAttendanceStatusRequest statusRequest = SeminarAttendanceStatusRequest.builder()
          .excuse("늦게 일어나서")
          .statusType(LATENESS)
          .build();

      startSeminarUsingApi(adminToken, seminarId, seminarStartRequest).andExpect(status().isOk());

      changeAttendanceStatusUsingApi(adminToken, seminarId, adminId, statusRequest)
          .andExpect(status().isNoContent())
          .andDo(document("change-attendance-seminar",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName()).description(
                      "ACCESS TOKEN %s".formatted(securedValue))),
              pathParameters(
                  parameterWithName("memberId")
                      .description("출석 상태를 변경하고자 하는 회원의 ID"),
                  parameterWithName("seminarId")
                      .description("세미나의 ID")
              ),
              requestFields(
                  field("excuse", "세미나 사유"),
                  field("statusType", "출석 타입"))
          )).andReturn();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"\"\""})
    @DisplayName("출석 사유를 적지 않아도 상태 변경을 성공한다.")
    public void should_success_when_emptyExcuse(String excuse) throws Exception {
      String strJson = """
          {"excuse":%s, "statusType":"LATENESS"}""";
      Long seminarId = createSeminarAndGetId(adminToken);
      String attendanceCode = seminarRepository.findById(seminarId).orElseThrow()
          .getAttendanceCode();

      SeminarAttendanceCodeRequest request = SeminarAttendanceCodeRequest.builder()
          .attendanceCode(attendanceCode)
          .build();

      startSeminarUsingApi(adminToken, seminarId, seminarStartRequest).andExpect(status().isOk());
      changeAttendanceStatusUsingApi(adminToken, seminarId, adminId, strJson.formatted(excuse)).andExpect(
          status().isNoContent());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"\"asdf\""})
    @DisplayName("비정상적인 값이 들어가면 실패한다.")
    public void should_fail_when_invalidValue(String statusType) throws Exception {
      String strJson = """
          {"excuse":"", "statusType": %s}""";
      Long seminarId = createSeminarAndGetId(adminToken);
      String attendanceCode = seminarRepository.findById(seminarId).orElseThrow()
          .getAttendanceCode();

      SeminarAttendanceCodeRequest request = SeminarAttendanceCodeRequest.builder()
          .attendanceCode(attendanceCode)
          .build();

      startSeminarUsingApi(adminToken, seminarId, seminarStartRequest).andExpect(status().isOk());
      changeAttendanceStatusUsingApi(adminToken, seminarId, adminId, strJson.formatted(statusType)).andExpect(
          status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("세미나 출석 정보 목록 조회 테스트")
  class GetSeminarAttendanceTest {

    @Test
    @DisplayName("유효한 요청일 경우 세미나 출석 목록 조회는 성공한다.")
    public void 유효한_요청일_경우_세미나_출석_목록_조회는_성공한다() throws Exception {
      String securedValue = getSecuredValue(SeminarAttendanceController.class, "getAttendances");
      // given
      Long seminarId = seminarService.save(LocalDate.now()).id();
      Member admin = memberRepository.findById(adminId).orElseThrow();
      String attendanceCode = seminarService.start(admin, seminarId, LocalDateTime.now().minusMinutes(5),
          LocalDateTime.now().plusMinutes(5)).attendanceCode();
      Member user = memberRepository.findById(userId).orElseThrow();
      Long attendanceId = seminarAttendanceService.attendance(seminarId, user, attendanceCode).getId();

      em.flush();
      em.clear();
      // then
      mockMvc.perform(get("/seminars/attendances")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content[0].memberId").value(adminId))
          .andExpect(jsonPath("$.content[0].memberName").value(admin.getRealName()))
          .andExpect(jsonPath("$.content[0].generation").value(admin.getGeneration()))
          .andExpect(jsonPath("$.content[0].attendances[0].attendanceStatus").value(ATTENDANCE.toString()))
          .andExpect(jsonPath("$.content[0].attendances[0].excuse").isEmpty())
          .andExpect(jsonPath("$.content[0].attendances[0].attendDate").value(LocalDate.now().toString()))
          .andExpect(jsonPath("$.content[1].memberId").value(userId))
          .andExpect(jsonPath("$.content[1].memberName").value(user.getRealName()))
          .andExpect(jsonPath("$.content[1].generation").value(user.getGeneration()))
          .andExpect(jsonPath("$.content[1].attendances[0].attendanceId").value(attendanceId))
          .andExpect(jsonPath("$.content[1].attendances[0].attendanceStatus").value(LATENESS.toString()))
          .andExpect(jsonPath("$.content[1].attendances[0].excuse").isEmpty())
          .andExpect(jsonPath("$.content[1].attendances[0].attendDate").value(LocalDate.now().toString()))
          .andDo(document("get-seminar-attendances",
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
                  pageHelper(getAttendances())
              )));
    }

    FieldDescriptor[] getAttendances() {
      return new FieldDescriptor[]{
          fieldWithPath("memberId").description("회원 ID"),
          fieldWithPath("memberName").description("회원 이름"),
          fieldWithPath("generation").description("회원 기수"),
          fieldWithPath("attendances[]").description("회원 출석 정보 리스트"),
          fieldWithPath("attendances[].attendanceId").description("세미나 출석 ID"),
          fieldWithPath("attendances[].attendDate").description("세미나 출석 날짜"),
          fieldWithPath("attendances[].attendanceStatus").description("세미나 출석 상태"),
          fieldWithPath("attendances[].excuse").description("세미나 결석/지각 사유").optional(),
      };
    }
  }
}
