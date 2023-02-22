package com.keeper.homepage.domain.seminar.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.ABSENCE;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.ATTENDANCE;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.LATENESS;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.field;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.domain.seminar.dto.request.SeminarAttendanceRequest;
import com.keeper.homepage.domain.seminar.dto.request.SeminarStartRequest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarAttendanceResponse;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

      SeminarAttendanceRequest request = SeminarAttendanceRequest.builder()
          .id(seminarId)
          .attendanceCode(attendanceCode)
          .build();

      startSeminarUsingApi(adminToken, seminarId, seminarStartRequest).andExpect(status().isOk());

      MvcResult mvcResult = attendanceSeminarUsingApi(adminToken, request)
          .andExpect(status().isOk())
          .andDo(document("attendance-seminar",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName()).description(
                      "ACCESS TOKEN %s".formatted(securedValue))),
              requestFields(
                  field("id", "세미나 ID"),
                  field("attendanceCode", "세미나 출석 코드")),
              responseFields(
                  field("id", "세미나 출석 ID"),
                  field("attendanceStatus", "출석 상태"))
          )).andReturn();

      SeminarAttendanceStatusType statusType = objectMapper.readValue(
          mvcResult.getResponse().getContentAsString(),
          SeminarAttendanceResponse.class).getAttendanceStatus();

      assertThat(statusType).isEqualTo(ATTENDANCE);
    }

    @Test
    @DisplayName("세미나 중복 출석을 실패한다.")
    public void shoud_fail_attendanceSeminarDuplicate() throws Exception {
      Long seminarId = createSeminarAndGetId(adminToken);
      String attendanceCode = seminarRepository.findById(seminarId).orElseThrow()
          .getAttendanceCode();

      SeminarAttendanceRequest request = SeminarAttendanceRequest.builder()
          .id(seminarId)
          .attendanceCode(attendanceCode)
          .build();

      startSeminarUsingApi(adminToken, seminarId, seminarStartRequest).andExpect(status().isOk());
      attendanceSeminarUsingApi(adminToken, request).andExpect(status().isOk());
      em.flush();
      em.clear();

      attendanceSeminarUsingApi(adminToken, request).andExpect(status().isConflict());
    }

    @Test
    @DisplayName("비정상적인 입력을 시도했을 때 실패한다.")
    public void should_fail_invalidInputValue() throws Exception {
      Long seminarId = createSeminarAndGetId(adminToken);
      String attendanceCode = seminarRepository.findById(seminarId).orElseThrow()
          .getAttendanceCode();

      startSeminarUsingApi(adminToken, seminarId, seminarStartRequest).andExpect(status().isOk());

      var strJsonList = Arrays.asList(
          """
          {"id":null, "attendanceCode":null}""",
          """
          {"id":%s, "attendanceCode":null}""".formatted(seminarId),
          """
          {"id":"%s", "attendanceCode":null}""".formatted(seminarId),
          """
          {"id":null, "attendanceCode":""}""",
          """
          {"id":%s, "attendanceCode":""}""".formatted(seminarId),
          """
          {"id":"%s", "attendanceCode":""}""".formatted(seminarId),
          """
          {"id":null, "attendanceCode":%s}""".formatted(attendanceCode));

      strJsonList.stream().forEach(i -> {
        try {
          attendanceSeminarUsingApi(adminToken, i).andExpect(status().isBadRequest());
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    }

    @Test
    @DisplayName("세미나 지각을 성공한다.")
    public void should_success_when_attendanceSeminarLateness() throws Exception {
      Long seminarId = seminarTestHelper.builder()
          .attendanceCloseTime(now.plusMinutes(-2))
          .latenessCloseTime(now.plusMinutes(3))
          .build().getId();
      String attendanceCode = seminarRepository.findById(seminarId).orElseThrow()
          .getAttendanceCode();

      SeminarAttendanceRequest request = SeminarAttendanceRequest.builder()
          .id(seminarId)
          .attendanceCode(attendanceCode)
          .build();

      MvcResult mvcResult = attendanceSeminarUsingApi(adminToken, request)
          .andExpect(status().isOk()).andReturn();

      SeminarAttendanceStatusType statusType = objectMapper.readValue(
          mvcResult.getResponse().getContentAsString(),
          SeminarAttendanceResponse.class).getAttendanceStatus();

      assertThat(statusType).isEqualTo(LATENESS);
    }

    @Test
    @DisplayName("세미나 지각 마감 시간이 지나면 결석 처리한다.")
    public void should_success_when_attendanceSeminarAbsence() throws Exception {
      Long seminarId = seminarTestHelper.builder()
          .attendanceCloseTime(now.plusMinutes(-2))
          .latenessCloseTime(now.plusMinutes(-1))
          .build().getId();
      String attendanceCode = seminarRepository.findById(seminarId).orElseThrow()
          .getAttendanceCode();

      SeminarAttendanceRequest request = SeminarAttendanceRequest.builder()
          .id(seminarId)
          .attendanceCode(attendanceCode)
          .build();

      MvcResult mvcResult = attendanceSeminarUsingApi(adminToken, request)
          .andExpect(status().isOk()).andReturn();

      SeminarAttendanceStatusType statusType = objectMapper.readValue(
          mvcResult.getResponse().getContentAsString(),
          SeminarAttendanceResponse.class).getAttendanceStatus();

      assertThat(statusType).isEqualTo(ABSENCE);
    }
  }
}
