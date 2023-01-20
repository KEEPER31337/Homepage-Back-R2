package com.keeper.homepage.domain.auth.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static com.keeper.homepage.global.config.security.JwtTokenProvider.ACCESS_TOKEN;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AuthControllerTest extends IntegrationTest {

  private final long adminId = 0L;
  private final long userId = 1L;
  private String adminToken;
  private String userToken;

  @BeforeEach
  void setup() {
    adminToken = jwtTokenProvider.createAccessToken(adminId, ROLE_회원, ROLE_회장);
    userToken = jwtTokenProvider.createAccessToken(userId, ROLE_회원);
  }

  @Nested
  @DisplayName("/auth-test")
  class NoToken {

    @Test
    @DisplayName("토큰이 없어도 permit-all 설정의 URL은 접근이 가능해야 한다.")
    void success_noToken() throws Exception {
      mockMvc.perform(get("/auth-test"))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원 권한의 토큰으로 permit-all 설정의 URL은 접근이 가능해야 한다.")
    void success_userToken() throws Exception {
      mockMvc.perform(get("/auth-test")
              .cookie(new Cookie(ACCESS_TOKEN, userToken)))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회장 권한의 토큰으로 permit-all 설정의 URL은 접근이 가능해야 한다.")
    void success_adminToken() throws Exception {
      mockMvc.perform(get("/auth-test")
              .cookie(new Cookie(ACCESS_TOKEN, adminToken)))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("/auth-test/user")
  class UserToken {

    private static final String USER_URL = "/auth-test/user";

    @Test
    @DisplayName("토큰이 없을 때 permit-all 설정이 아닌 URL은 401을 반환해야 한다.")
    void should_return401_when_noTokenAccessUser() throws Exception {
      mockMvc.perform(get(USER_URL))
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("회원 권한의 token에서 @AuthId로 id가 가져와져야 한다.")
    void userToken() throws Exception {
      mockMvc.perform(get(USER_URL)
              .cookie(new Cookie(ACCESS_TOKEN, userToken)))
          .andExpect(status().isOk())
          .andExpect(content().string(String.valueOf(userId)));
    }
  }

  @Nested
  @DisplayName("/auth-test/admin")
  class AdminToken {

    private static final String ADMIN_URL = "/auth-test/admin";

    @Test
    @DisplayName("토큰이 없을 때 회장 접근 권한의 URL은 401을 반환해야 한다.")
    void should_return401_when_noTokenAccessUser() throws Exception {
      mockMvc.perform(get(ADMIN_URL))
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("user권한의 토큰일 때 회장 접근 권한의 URL은 403을 반환해야 한다.")
    void success_noToken() throws Exception {
      mockMvc.perform(get(ADMIN_URL)
              .cookie(new Cookie(ACCESS_TOKEN, userToken)))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("회장 권한의 token에서 @AuthId로 id가 가져와져야 한다.")
    void adminToken() throws Exception {
      mockMvc.perform(get(ADMIN_URL)
              .cookie(new Cookie(ACCESS_TOKEN, adminToken)))
          .andExpect(status().isOk())
          .andExpect(content().string(String.valueOf(adminId)));
    }
  }
}
