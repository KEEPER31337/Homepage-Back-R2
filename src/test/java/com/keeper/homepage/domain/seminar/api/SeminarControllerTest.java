package com.keeper.homepage.domain.seminar.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.seminar.dto.request.SeminarSaveRequest;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

public class SeminarControllerTest extends IntegrationTest {

  private long adminId;
  private long userId;
  private String adminToken;
  private String userToken;
  private SeminarSaveRequest seminarSaveRequest;
  private static final Long virtualSeminarId = 1L;

  @BeforeEach
  void setUp() {
    adminId = memberTestHelper.builder().build().getId();
    userId = memberTestHelper.builder().build().getId();
    adminToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, adminId, ROLE_회원, ROLE_회장);
    userToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, userId, ROLE_회원);

    LocalDateTime now = LocalDateTime.now();
    seminarSaveRequest = SeminarSaveRequest.builder()
        .attendanceCloseTime(now.plusMinutes(3))
        .latenessCloseTime(now.plusMinutes(4)).build();
  }

  ResultActions makeSeminarUsingApi(String token) throws Exception {
    return mockMvc.perform(post("/seminars")
        .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), token))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(seminarSaveRequest)));
  }

  ResultActions searchSeminarUsingApi(String token) throws Exception {
    return mockMvc.perform(get("/seminars")
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
      makeSeminarUsingApi(adminToken).andExpect(status().isOk());
    }

    @Test
    @DisplayName("관리자 권한이 아니면 세미나 등록을 실패한다.")
    public void should_failCreateSeminar_when_notAdmin() throws Exception {
      makeSeminarUsingApi(userToken).andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("세미나 조회 테스트")
  class SeminarCheckTest {

    @Test
    @DisplayName("관리자 권한으로 생성된 세미나의 개수를 확인한다.")
    public void should_checkCountSeminar_when_admin() throws Exception {
      makeSeminarUsingApi(adminToken).andExpect(status().isOk());
      em.flush();
      em.clear();

      searchSeminarUsingApi(adminToken)
          // virtual_seminar 컬럼을 포함하여 생성된 세미나는 2개가 존재한다.
          .andExpect(jsonPath("$.length()", is(2)))
          .andExpect(jsonPath("$[1].openTime").exists())
          .andExpect(jsonPath("$[1].attendanceCloseTime").exists())
          .andExpect(jsonPath("$[1].latenessCloseTime").exists())
          .andExpect(jsonPath("$[1].attendanceCode").exists())
          .andExpect(jsonPath("$[1].name").exists())
          .andExpect(jsonPath("$[1].registerTime").exists())
          .andExpect(jsonPath("$[1].updateTime").exists());
    }

    @Test
    @DisplayName("관리자 권한이 아니면 세미나 조회를 실패한다.")
    public void should_failCountSeminar_when_notAdmin() throws Exception {
      makeSeminarUsingApi(adminToken).andExpect(status().isOk());
      searchSeminarUsingApi(userToken).andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("세미나 삭제 테스트")
  class SeminarDeleteTest {

    @Test
    @DisplayName("관리자 권한으로 세미나 삭제를 성공한다.")
    public void should_successDeleteSeminar_when_admin() throws Exception {
      makeSeminarUsingApi(adminToken).andExpect(status().isOk());
      Long seminarId = seminarRepository.findAll().stream()
          .filter(i -> i.getId() != virtualSeminarId)
          .findAny().orElseThrow()
          .getId();

      deleteSeminarUsingApi(adminToken, seminarId).andExpect(status().isOk());
      searchSeminarUsingApi(adminToken).andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    @DisplayName("관리자 권한이 아니면 세미나 삭제를 실패한다.")
    public void should_failDeleteSeminar_when_notAdmin() throws Exception {
      makeSeminarUsingApi(adminToken).andExpect(status().isOk());
      Long seminarId = seminarRepository.findAll().stream()
          .filter(i -> i.getId() != virtualSeminarId)
          .findAny().orElseThrow()
          .getId();

      deleteSeminarUsingApi(userToken, seminarId).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("존재하지 않는 세미나를 삭제했을 때 실패한다.")
    public void should_failDeleteNotExistsSeminar_when_admin() throws Exception {
      makeSeminarUsingApi(adminToken).andExpect(status().isOk());
      deleteSeminarUsingApi(adminToken, -1L).andExpect(status().isNotFound());
    }
  }
}
