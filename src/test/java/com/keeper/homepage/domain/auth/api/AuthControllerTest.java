package com.keeper.homepage.domain.auth.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.config.security.data.JwtType.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

class AuthControllerTest extends IntegrationTest {

  private final long adminId = 0L;
  private final long userId = 1L;
  private String adminToken;
  private String userToken;

  @BeforeEach
  void setup() {
    adminToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, adminId, ROLE_회원, ROLE_회장);
    userToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, userId, ROLE_회원);
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
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), userToken)))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회장 권한의 토큰으로 permit-all 설정의 URL은 접근이 가능해야 한다.")
    void success_adminToken() throws Exception {
      mockMvc.perform(get("/auth-test")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken)))
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
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), userToken)))
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
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), userToken)))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("회장 권한의 token에서 @AuthId로 id가 가져와져야 한다.")
    void adminToken() throws Exception {
      mockMvc.perform(get(ADMIN_URL)
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminToken)))
          .andExpect(status().isOk())
          .andExpect(content().string(String.valueOf(adminId)));
    }
  }

  @Nested
  @DisplayName("/auth-test/refresh")
  class RefreshToken {

    private static final String REFRESH_URL = "/auth-test/refresh";

    @Nested
    @DisplayName("RT가 없을 때")
    class NotExist {

      @Test
      @DisplayName("AT만 있어도 200 OK를 응답한다.")
      void should_200OK_when_accessTokenExist() throws Exception {
        Cookie accessCookie = new Cookie(ACCESS_TOKEN.getTokenName(), adminToken);
        accessCookie.setHttpOnly(true);

        callRefreshApi(accessCookie)
            .andExpect(status().isOk())
            .andExpect(content().string(String.valueOf(adminId)));
      }

      @Test
      @DisplayName("AT도 만료되었으면 401 Unauthorization을 응답한다.")
      void should_401Unauthorization_when_accessTokenExpired() throws Exception {
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZXMiOiJST0xFX-2ajOybkCIsImlhdCI6MTY3NDQ1MjM1NSwiZXhwIjoxNjc0NDUyMzU1fQ.FoRbgOGlzLwizp9jQNmM6pET4zA8TPXa56zZlsl6Al8";
        Cookie expiredCookie = new Cookie(ACCESS_TOKEN.getTokenName(), expiredToken);
        expiredCookie.setHttpOnly(true);

        callRefreshApi(expiredCookie)
            .andExpect(status().isUnauthorized());
      }

      @Test
      @DisplayName("AT도 없으면 401 Unauthorization을 응답한다.")
      void should_401Unauthorization_when_accessTokenNotExist() throws Exception {
        callRefreshApi()
            .andExpect(status().isUnauthorized());
      }
    }

    @Nested
    @DisplayName("RT가 있을 때")
    class Exist {

      // PK: 0
      // ROLE: 회원
      // expired: 2073년 1월 24일
      String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwIiwicm9sZXMiOiJST0xFX-2ajOybkCIsImlhdCI6MTY3NDUxODc5OCwiZXhwIjozMjUxMzE4Nzk4fQ.mYiEM9LfwmTJccXYUgwuIhnZzWE74TUgX0ETi9lVZRI";
      Cookie refreshCookie = new Cookie(REFRESH_TOKEN.getTokenName(), refreshToken);

      @BeforeEach
      void setupRefreshToken() {
        redisUtil.setDataExpire(String.valueOf(0), refreshToken, REFRESH_TOKEN.getExpiredMillis());
      }

      @Test
      @DisplayName("AT가 만료되었으면 200 OK를 응답한다.")
      void should_200OK_when_accessTokenExpired() throws Exception {
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZXMiOiJST0xFX-2ajOybkCIsImlhdCI6MTY3NDQ1MjM1NSwiZXhwIjoxNjc0NDUyMzU1fQ.FoRbgOGlzLwizp9jQNmM6pET4zA8TPXa56zZlsl6Al8";
        Cookie expiredCookie = new Cookie(ACCESS_TOKEN.getTokenName(), expiredToken);
        expiredCookie.setHttpOnly(true);
        assertThatThrownBy(() -> jwtTokenProvider.getAuthId(expiredToken))
            .isInstanceOf(ExpiredJwtException.class);

        MvcResult result = callRefreshApi(refreshCookie, expiredCookie)
            .andExpect(status().isOk())
            .andReturn();

        String newAccessToken = Objects.requireNonNull(
                result.getResponse().getCookie(ACCESS_TOKEN.getTokenName()))
            .getValue();
        String newRefreshToken = Objects.requireNonNull(
                result.getResponse().getCookie(REFRESH_TOKEN.getTokenName()))
            .getValue();

        jwtTokenProvider.getAuthId(newAccessToken);
        assertThat(newRefreshToken).isNotEqualTo(refreshCookie.getValue());
        assertThat(redisUtil.getData("0")).isNotEmpty();
      }

      @Test
      @DisplayName("AT도 있으면 200 OK를 응답한다.")
      void should_200OK_when_accessTokenExist() throws Exception {
        Cookie accessCookie = new Cookie(ACCESS_TOKEN.getTokenName(), adminToken);
        accessCookie.setHttpOnly(true);

        MvcResult result = callRefreshApi(refreshCookie, accessCookie)
            .andExpect(status().isOk())
            .andReturn();
      }

      @Test
      @DisplayName("AT가 없으면 401 Unauthorization을 응답한다")
      void should_401Unauthorizatio_when_accessTokenNotExist() throws Exception {
        callRefreshApi(refreshCookie)
            .andExpect(status().isUnauthorized());
      }
    }

    @Nested
    @DisplayName("RT가 만료되었을 때")
    class Expired {

    }

    private ResultActions callRefreshApi(Cookie... cookies) throws Exception {
      if (cookies == null || cookies.length == 0) {
        return mockMvc.perform(get(REFRESH_URL));
      }
      return mockMvc.perform(get(REFRESH_URL).cookie(cookies));
    }
  }
}
