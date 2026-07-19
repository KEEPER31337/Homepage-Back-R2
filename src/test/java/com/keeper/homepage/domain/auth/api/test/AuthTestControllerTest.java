package com.keeper.homepage.domain.auth.api.test;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static com.keeper.homepage.global.config.security.session.SessionPolicy.SESSION_COOKIE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.global.config.security.session.SessionData;
import com.keeper.homepage.global.config.security.session.SessionIdCodec;
import com.keeper.homepage.global.config.security.session.SessionPolicy;
import jakarta.servlet.http.Cookie;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

class AuthTestControllerTest extends IntegrationTest {

  private static final String USER_URL = "/auth-test/user";
  private static final String ADMIN_URL = "/auth-test/admin";
  private static final String SESSION_URL = "/auth-test/session";

  @Autowired
  private SessionIdCodec sessionIdCodec;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  private long adminId;
  private long userId;
  private String adminSessionId;
  private String userSessionId;

  @BeforeEach
  void setup() {
    adminId = memberTestHelper.builder().build().getId();
    userId = memberTestHelper.builder().build().getId();
    adminSessionId = sessionService.createSessionId(adminId, ROLE_회원, ROLE_회장);
    userSessionId = sessionService.createSessionId(userId, ROLE_회원);
  }

  @Nested
  @DisplayName("permit-all URL")
  class PermitAll {

    @Test
    @DisplayName("세션이 없어도 접근할 수 있다.")
    void success_withoutSession() throws Exception {
      mockMvc.perform(get("/auth-test"))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유효한 세션으로도 접근할 수 있다.")
    void success_withSession() throws Exception {
      mockMvc.perform(get("/auth-test").cookie(sessionCookie(userSessionId)))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("세션 권한")
  class Authorization {

    @Test
    @DisplayName("세션이 없으면 보호된 URL은 401을 반환한다.")
    void unauthorized_withoutSession() throws Exception {
      mockMvc.perform(get(USER_URL))
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("회원 세션에서 로그인 회원 ID를 가져온다.")
    void resolvesUserId() throws Exception {
      mockMvc.perform(get(USER_URL).cookie(sessionCookie(userSessionId)))
          .andExpect(status().isOk())
          .andExpect(content().string(String.valueOf(userId)));
    }

    @Test
    @DisplayName("권한이 부족하면 403을 반환한다.")
    void forbidden_withInsufficientRole() throws Exception {
      mockMvc.perform(get(ADMIN_URL).cookie(sessionCookie(userSessionId)))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("세션에 저장된 복수 권한을 적용한다.")
    void appliesMultipleRoles() throws Exception {
      mockMvc.perform(get(ADMIN_URL).cookie(sessionCookie(adminSessionId)))
          .andExpect(status().isOk())
          .andExpect(content().string(String.valueOf(adminId)));
    }
  }

  @Nested
  @DisplayName("세션 수명")
  class Lifetime {

    @Test
    @DisplayName("유효한 세션 ID는 그대로 인증에 사용한다.")
    void validSession() throws Exception {
      mockMvc.perform(get(SESSION_URL).cookie(sessionCookie(userSessionId)))
          .andExpect(status().isOk())
          .andExpect(content().string("session!"))
          .andExpect(cookie().doesNotExist(SESSION_COOKIE_NAME));
    }

    @Test
    @DisplayName("idle TTL이 절반 이하이면 Redis TTL과 쿠키를 7일로 연장한다.")
    void touchSession() throws Exception {
      String key = keyOf(userSessionId);
      stringRedisTemplate.expire(key, Duration.ofDays(3));

      mockMvc.perform(get(SESSION_URL).cookie(sessionCookie(userSessionId)))
          .andExpect(status().isOk())
          .andExpect(cookie().value(SESSION_COOKIE_NAME, userSessionId))
          .andExpect(cookie().maxAge(SESSION_COOKIE_NAME,
              Math.toIntExact(SessionPolicy.IDLE_TIMEOUT.toSeconds())));

      Long ttl = stringRedisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
      assertThat(ttl).isGreaterThan(Duration.ofDays(6).toMillis());
      assertThat(ttl).isLessThanOrEqualTo(SessionPolicy.IDLE_TIMEOUT.toMillis());
    }

    @Test
    @DisplayName("idle TTL이 절반보다 많이 남으면 연장하지 않는다.")
    void doesNotTouchEarly() throws Exception {
      String key = keyOf(userSessionId);
      stringRedisTemplate.expire(key, Duration.ofDays(4));

      mockMvc.perform(get(SESSION_URL).cookie(sessionCookie(userSessionId)))
          .andExpect(status().isOk())
          .andExpect(cookie().doesNotExist(SESSION_COOKIE_NAME));

      assertThat(stringRedisTemplate.getExpire(key, TimeUnit.MILLISECONDS))
          .isLessThanOrEqualTo(Duration.ofDays(4).toMillis());
    }

    @Test
    @DisplayName("Redis에 세션이 없으면 인증하지 않고 쿠키도 지우지 않는다.")
    void invalidSession() throws Exception {
      stringRedisTemplate.delete(keyOf(userSessionId));

      mockMvc.perform(get(SESSION_URL).cookie(sessionCookie(userSessionId)))
          .andExpect(status().isUnauthorized())
          .andExpect(cookie().doesNotExist(SESSION_COOKIE_NAME));
    }

    @Test
    @DisplayName("TTL이 유실된 세션은 삭제하고 인증하지 않는다.")
    void deleteSessionWithoutTtl() throws Exception {
      String key = keyOf(userSessionId);
      stringRedisTemplate.persist(key);

      mockMvc.perform(get(SESSION_URL).cookie(sessionCookie(userSessionId)))
          .andExpect(status().isUnauthorized());

      assertThat(stringRedisTemplate.hasKey(key)).isFalse();
    }

    @Test
    @DisplayName("absolute 만료 시각에 도달한 세션은 삭제하고 인증하지 않는다.")
    void deleteAbsolutelyExpiredSession() throws Exception {
      String key = keyOf(userSessionId);
      SessionData expired = new SessionData(userId, 0, 0, java.util.List.of(ROLE_회원.name()));
      stringRedisTemplate.opsForValue()
          .set(key, asJsonString(expired), Duration.ofDays(1));

      mockMvc.perform(get(SESSION_URL).cookie(sessionCookie(userSessionId)))
          .andExpect(status().isUnauthorized());

      assertThat(stringRedisTemplate.hasKey(key)).isFalse();
    }

    @Test
    @DisplayName("정규 형식이 아닌 세션 ID는 Redis 조회 없이 거부한다.")
    void rejectMalformedSessionId() throws Exception {
      mockMvc.perform(get(SESSION_URL).cookie(sessionCookie("not-a-session-id")))
          .andExpect(status().isUnauthorized())
          .andExpect(cookie().doesNotExist(SESSION_COOKIE_NAME));
    }
  }

  private Cookie sessionCookie(String sessionId) {
    return new Cookie(SESSION_COOKIE_NAME, sessionId);
  }

  private String keyOf(String sessionId) {
    return sessionIdCodec.toRedisKey(sessionId).orElseThrow();
  }
}
