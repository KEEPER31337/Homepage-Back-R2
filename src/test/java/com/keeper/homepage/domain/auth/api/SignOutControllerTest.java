package com.keeper.homepage.domain.auth.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.global.config.security.session.SessionPolicy.SESSION_COOKIE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.config.security.session.SessionIdCodec;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

class SignOutControllerTest extends IntegrationTest {

  @Autowired
  private SessionIdCodec sessionIdCodec;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Nested
  @DisplayName("로그아웃 테스트")
  class SignOut {

    private Member member;

    @BeforeEach
    void setupMember() {
      member = memberTestHelper.generate();
    }

    @Test
    @DisplayName("유효한 요청이면 세션을 삭제하고 로그아웃해야 한다.")
    void should_successfullySignOut_when_validRequest() throws Exception {
      String sessionId = sessionService.createSessionId(member.getId(), ROLE_회원);
      String key = sessionIdCodec.toRedisKey(sessionId).orElseThrow();
      Cookie sessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);

      mockMvc.perform(post("/sign-out").cookie(sessionCookie))
          .andExpect(status().isNoContent())
          .andExpect(cookie().maxAge(SESSION_COOKIE_NAME, 0))
          .andDo(document("sign-out",
              requestCookies(
                  cookieWithName(SESSION_COOKIE_NAME).description("OPAQUE SESSION ID")
              )));

      assertThat(stringRedisTemplate.hasKey(key)).isFalse();
    }
  }
}
