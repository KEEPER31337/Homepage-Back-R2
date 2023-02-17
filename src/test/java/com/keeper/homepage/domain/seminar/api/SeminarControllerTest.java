package com.keeper.homepage.domain.seminar.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_서기;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.seminar.dto.request.SeminarSaveRequest;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;

public class SeminarControllerTest extends IntegrationTest {

  private long adminId;
  private long clerkId;
  private long userId;
  private String adminToken;
  private String clerkToken;
  private String userToken;
  private LocalDateTime now;
  private SeminarSaveRequest seminarSaveRequest;

  @BeforeEach
  void setUp() {
    adminId = memberTestHelper.builder().build().getId();
    clerkId = memberTestHelper.builder().build().getId();
    userId = memberTestHelper.builder().build().getId();
    adminToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, adminId, ROLE_회원, ROLE_회장);
    clerkToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, clerkId, ROLE_회원, ROLE_서기);
    userToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, userId, ROLE_회원);

    now = LocalDateTime.now();
    seminarSaveRequest = SeminarSaveRequest.builder()
        .attendanceCloseTime(now.plusMinutes(3))
        .latenessCloseTime(now.plusMinutes(4)).build();
  }

  ResultActions makeSeminarUsingApi(String token, SeminarSaveRequest request) throws Exception {
    return mockMvc.perform(post("/seminars")
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), token))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));
  }

  ResultActions searchAllSeminarUsingApi(String token) throws Exception {
    return mockMvc.perform(get("/seminars")
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), token)));
  }

  ResultActions searchSeminarUsingApi(String token, String idx) throws Exception {
    return mockMvc.perform(get("/seminars/" + idx)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), token)));
  }

  ResultActions searchDateSeminarUsingApi(String token, String date) throws Exception {
    return mockMvc.perform(get("/seminars?date=" + date)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), token)));
  }

  ResultActions deleteSeminarUsingApi(String token, Long id) throws Exception {
    return mockMvc.perform(delete("/seminars/" + id)
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), token)));
  }

  @Nested
  @DisplayName("세미나 등록 테스트")
  class SeminarCreateTest {

    @Test
    @DisplayName("관리자 권한으로 세미나 등록을 성공한다.")
    public void should_successCreateSeminar_when_admin() throws Exception {
      makeSeminarUsingApi(adminToken, seminarSaveRequest).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("올바르지 않은 값이 들어왔을 때 세미나 등록을 실패한다.")
    public void should_failCreateSeminar_when_NotValidValue() throws Exception {
      makeSeminarUsingApi(adminToken, null).andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("세미나 출석 마감 시간 또는 지각 마감 시간이 과거인 경우 등록을 실패한다.")
    public void should_failCreateSeminar_when_closeTimeIsPast() throws Exception {
      seminarSaveRequest = SeminarSaveRequest.builder()
          .attendanceCloseTime(now.plusMinutes(-5))
          .latenessCloseTime(now.plusMinutes(-3)).build();
      makeSeminarUsingApi(adminToken, seminarSaveRequest).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("세미나 출석 마감 시간이 지각 마감 시간보다 미래인 경우 등록을 실패한다.")
    public void should_failCreateSeminar_when_attendanceTimeGreaterThanCloseTime() throws Exception {
      seminarSaveRequest = SeminarSaveRequest.builder()
          .attendanceCloseTime(now.plusMinutes(5))
          .latenessCloseTime(now.plusMinutes(3)).build();
      makeSeminarUsingApi(adminToken, seminarSaveRequest).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("관리자 권한이 아니면 세미나 등록을 실패한다.")
    public void should_failCreateSeminar_when_notAdmin() throws Exception {
      makeSeminarUsingApi(userToken, seminarSaveRequest).andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("세미나 조회 테스트")
  class SeminarCheckTest {

    @Test
    @DisplayName("생성된 세미나의 개수 및 데이터를 확인한다.")
    public void should_checkCountSeminar_when_admin() throws Exception {
      int beforeLength = validSeminarFindService.findAll().size();
      makeSeminarUsingApi(adminToken, seminarSaveRequest).andExpect(status().isCreated());
      em.flush();
      em.clear();

      int afterLength = validSeminarFindService.findAll().size();
      assertThat(afterLength).isEqualTo(beforeLength + 1);

      // 데이터가 추가 되었음을 위 코드에서 검증했기 때문에 추가된 데이터들이 정상적인지 확인
      // 조회된 모든 데이터를 검증하는 것은 비효율적이라고 생각이 들었다.
      int idx = afterLength - 1;
      searchAllSeminarUsingApi(adminToken)
          .andExpect(jsonPath("$.length()", is(afterLength)))
          .andExpectAll(combineJsonPath("openTime", idx).exists())
          .andExpect(combineJsonPath("openTime", idx).exists())
          .andExpect(combineJsonPath("attendanceCloseTime", idx).exists())
          .andExpect(combineJsonPath("latenessCloseTime", idx).exists())
          .andExpect(combineJsonPath("attendanceCode", idx).exists())
          .andExpect(combineJsonPath("name", idx).exists())
          .andExpect(combineJsonPath("registerTime", idx).exists())
          .andExpect(combineJsonPath("updateTime", idx).exists());
    }

    public JsonPathResultMatchers combineJsonPath(String name, int idx) {
      String parseFormat = "$[%d].%s";
      return jsonPath(parseFormat, idx, name);
    }

    @Test
    @DisplayName("관리자 권한이 아니면 세미나 조회를 실패한다.")
    public void should_failSearchSeminar_when_notAdmin() throws Exception {
      makeSeminarUsingApi(adminToken, seminarSaveRequest).andExpect(status().isCreated());
      searchAllSeminarUsingApi(userToken).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("id 값으로 세미나 조회를 성공한다.")
    public void should_successSearchSeminarUsingId_when_admin() throws Exception {
      MvcResult mvcResult = makeSeminarUsingApi(adminToken, seminarSaveRequest)
          .andExpect(status().isCreated()).andReturn();
      String seminarId = mvcResult.getResponse().getContentAsString();

      searchSeminarUsingApi(adminToken, seminarId).andExpect(status().isOk());
    }

    @Test
    @DisplayName("존재하지 않는 세미나를 조회했을 때 실패한다.")
    public void should_failSearchSeminarNotExistId_when_admin() throws Exception {
      searchSeminarUsingApi(adminToken, "0").andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("관리자 권한이 아니면 id 값으로 세미나 조회를 실패한다.")
    public void should_failSearchSeminarUsingId_when_notAdmin() throws Exception {
      searchSeminarUsingApi(userToken, "2").andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("세미나를 날짜로 필터링하여 조회한다.")
    public void should_searchSeminar_when_filterDate() throws Exception {
      makeSeminarUsingApi(adminToken, seminarSaveRequest).andExpect(status().isCreated());
      em.clear();

      searchDateSeminarUsingApi(adminToken, LocalDate.now().toString())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.openTime").exists())
          .andExpect(jsonPath("$.attendanceCloseTime").exists())
          .andExpect(jsonPath("$.latenessCloseTime").exists())
          .andExpect(jsonPath("$.attendanceCode").exists())
          .andExpect(jsonPath("$.name").exists())
          .andExpect(jsonPath("$.registerTime").exists())
          .andExpect(jsonPath("$.updateTime").exists());
    }

    @Test
    @DisplayName("서기는 날짜로 세미나를 조회할 수 없다.")
    public void should_badRequest_when_clerkSearchDate() throws Exception {
      makeSeminarUsingApi(adminToken, seminarSaveRequest).andExpect(status().isCreated());
      em.clear();

      searchDateSeminarUsingApi(clerkToken, LocalDate.now().toString()).andExpect(status().isForbidden());
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
      makeSeminarUsingApi(adminToken, seminarSaveRequest).andExpect(status().isCreated());
      searchDateSeminarUsingApi(userToken, LocalDate.now().toString())
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("세미나 삭제 테스트")
  class SeminarDeleteTest {

    @Test
    @DisplayName("관리자 권한으로 세미나 삭제를 성공한다.")
    public void should_successDeleteSeminar_when_admin() throws Exception {
      makeSeminarUsingApi(adminToken, seminarSaveRequest).andExpect(status().isCreated());

      int beforeLength = seminarRepository.findAll().size();
      Long seminarId = seminarRepository.findAll().stream()
          .findAny().orElseThrow()
          .getId();

      deleteSeminarUsingApi(adminToken, seminarId).andExpect(status().isNoContent());
      int afterLength = seminarRepository.findAll().size();

      assertThat(afterLength).isEqualTo(beforeLength - 1);
      searchAllSeminarUsingApi(adminToken).andExpect(jsonPath("$.length()", is(afterLength)));
    }

    @Test
    @DisplayName("관리자 권한이 아니면 세미나 삭제를 실패한다.")
    public void should_failDeleteSeminar_when_notAdmin() throws Exception {
      makeSeminarUsingApi(adminToken, seminarSaveRequest).andExpect(status().isCreated());
      Long seminarId = seminarRepository.findAll().stream()
          .findAny().orElseThrow()
          .getId();

      deleteSeminarUsingApi(userToken, seminarId).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("존재하지 않는 세미나를 삭제했을 때 실패한다.")
    public void should_failDeleteNotExistsSeminar_when_admin() throws Exception {
      deleteSeminarUsingApi(adminToken, -1L).andExpect(status().isNotFound());
    }
  }
}
