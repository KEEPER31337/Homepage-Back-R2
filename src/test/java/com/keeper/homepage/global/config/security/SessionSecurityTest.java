package com.keeper.homepage.global.config.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.domain.auth.application.AuthCookieService;
import com.keeper.homepage.global.config.security.session.SessionAuthenticationFactory;
import com.keeper.homepage.global.config.security.session.SessionData;
import com.keeper.homepage.global.config.security.session.SessionIdCodec;
import com.keeper.homepage.global.config.security.session.SessionIdGenerator;
import com.keeper.homepage.global.config.security.session.SessionPolicy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.GrantedAuthority;

class SessionSecurityTest {

  private final SessionIdGenerator sessionIdGenerator = new SessionIdGenerator();
  private final SessionIdCodec sessionIdCodec = new SessionIdCodec();

  @Test
  void createsCanonical256BitSessionIds() {
    Set<String> generated = new HashSet<>();

    for (int i = 0; i < 100; i++) {
      String sessionId = sessionIdGenerator.generate();
      assertThat(sessionId).hasSize(43).doesNotContain("=");
      assertThat(sessionIdCodec.isCanonicalSessionId(sessionId)).isTrue();
      generated.add(sessionId);
    }

    assertThat(generated).hasSize(100);
  }

  @Test
  void hashesSessionIdBeforeUsingItAsRedisKey() {
    String sessionId = sessionIdGenerator.generate();
    String key = sessionIdCodec.toRedisKey(sessionId).orElseThrow();

    assertThat(key).startsWith("session:").hasSize("session:".length() + 64);
    assertThat(key).doesNotContain(sessionId);
    assertThat(sessionIdCodec.toRedisKey(sessionId)).contains(key);
  }

  @Test
  void rejectsNonCanonicalSessionIds() {
    assertThat(sessionIdCodec.toRedisKey(null)).isEmpty();
    assertThat(sessionIdCodec.toRedisKey("")).isEmpty();
    assertThat(sessionIdCodec.toRedisKey("abc=")).isEmpty();
    assertThat(sessionIdCodec.toRedisKey("a".repeat(43))).isEmpty();
  }

  @Test
  void createsAuthenticationFromStoredSession() {
    var session = new SessionData(1, 100, 200,
        List.of("ROLE_회원", "ROLE_회장"));

    var authentication = new SessionAuthenticationFactory().create(session);

    assertThat(authentication.isAuthenticated()).isTrue();
    assertThat(authentication.getName()).isEqualTo("1");
    assertThat(authentication.getAuthorities())
        .extracting(GrantedAuthority::getAuthority)
        .containsExactly("ROLE_회원", "ROLE_회장");
  }

  @Test
  void setsHostOnlySecureSessionCookie() {
    var response = new MockHttpServletResponse();

    new AuthCookieService().setSessionCookie(
        response, sessionIdGenerator.generate(), SessionPolicy.IDLE_TIMEOUT.toMillis());

    assertThat(response.getHeader("Set-Cookie"))
        .startsWith("session_id=")
        .contains("Path=/")
        .contains("Max-Age=604800")
        .contains("Secure")
        .contains("HttpOnly")
        .contains("SameSite=Strict")
        .doesNotContain("Domain=");
  }

  @Test
  void expiresSessionCookieWithSameScope() {
    var response = new MockHttpServletResponse();

    new AuthCookieService().expireSessionCookie(response);

    assertThat(response.getHeader("Set-Cookie"))
        .startsWith("session_id=")
        .contains("Path=/")
        .contains("Max-Age=0")
        .contains("Secure")
        .contains("HttpOnly")
        .contains("SameSite=Strict")
        .doesNotContain("Domain=");
  }
}
