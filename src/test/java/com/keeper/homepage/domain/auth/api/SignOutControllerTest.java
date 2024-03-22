package com.keeper.homepage.domain.auth.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.config.security.data.JwtType.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import io.kotest.core.spec.style.AnnotationSpec.Ignore;
import jakarta.servlet.http.Cookie;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

class SignOutControllerTest extends IntegrationTest {

    @Nested
    @DisplayName("로그아웃 테스트")
    class SignOut {

        private Member member;

        @BeforeEach
        void setupMember() {
            member = memberTestHelper.generate();
        }

        @Test
        @DisplayName("유효한 요청이면 로그아웃이 성공해야 한다.")
        void should_successfullySignOut_when_validRequest() throws Exception {
            Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN.getTokenName(),
                    jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(), ROLE_회원));
            Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN.getTokenName(),
                    jwtTokenProvider.createAccessToken(REFRESH_TOKEN, member.getId(), ROLE_회원));
            callSignOutApi(accessTokenCookie, refreshTokenCookie)
                    .andExpect(status().isNoContent())
                    .andExpect(cookie().maxAge(ACCESS_TOKEN.getTokenName(), 0))
                    .andExpect(cookie().maxAge(REFRESH_TOKEN.getTokenName(), 0))
                    .andDo(document("sign-out",
                            requestCookies(
                                    cookieWithName(ACCESS_TOKEN.getTokenName()).description("ACCESS TOKEN"),
                                    cookieWithName(REFRESH_TOKEN.getTokenName()).description("REFRESH TOKEN")
                            )));

            assertThat(redisUtil.getData(String.valueOf(member.getId()), String.class)).isEmpty();
        }

//    @Test
//    @Disabled
//    @DisplayName("RT도 AT도 만료되었으면 로그아웃시에 쿠키는 지워져야 한다")
//    void should_tokenDeleted_when_expiredTokens() throws Exception {
//      // PK: 0
//      // ROLE: 회원
//      // expired: 2023년 1월 25일
//      String expiredRefreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwIiwicm9sZXMiOiJST0xFX-2ajOybkCIsImlhdCI6MTY3NDYzMDk2MCwiZXhwIjoxNjc0NjMwOTYwfQ.qcAfEzhDulqsl6HCg8dziVlJoTPORpSUi5sjbCqTg_E";
//      Cookie expiredRefreshCookie = new Cookie(REFRESH_TOKEN.getTokenName(), expiredRefreshToken);
//      String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZXMiOiJST0xFX-2ajOybkCIsImlhdCI6MTY3NDQ1MjM1NSwiZXhwIjoxNjc0NDUyMzU1fQ.FoRbgOGlzLwizp9jQNmM6pET4zA8TPXa56zZlsl6Al8";
//      Cookie expiredCookie = new Cookie(ACCESS_TOKEN.getTokenName(), expiredToken);
//
//      callSignOutApi(expiredCookie, expiredRefreshCookie)
//              .andExpect(status().isUnauthorized())
//              .andExpect(cookie().maxAge(ACCESS_TOKEN.getTokenName(), 0))
//              .andExpect(cookie().maxAge(REFRESH_TOKEN.getTokenName(), 0));
//    }

        @NotNull
        private ResultActions callSignOutApi(Cookie accessTokenCookie, Cookie refreshTokenCookie) throws Exception {
            return mockMvc.perform(post("/sign-out")
                    .cookie(accessTokenCookie, refreshTokenCookie));
        }
    }
}
