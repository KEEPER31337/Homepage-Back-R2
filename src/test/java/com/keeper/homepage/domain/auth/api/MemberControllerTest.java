package com.keeper.homepage.domain.auth.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.config.security.data.JwtType.REFRESH_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.auth.dto.request.FindLoginIdRequest;
import com.keeper.homepage.domain.auth.dto.request.IssueTmpPasswordRequest;
import com.keeper.homepage.domain.auth.dto.request.SignInRequest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.EmailAddress;
import com.keeper.homepage.domain.member.entity.embedded.LoginId;
import com.keeper.homepage.domain.member.entity.embedded.Password;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class SignInControllerTest extends IntegrationTest {

  @Nested
  @DisplayName("????????? ?????????")
  class SignIn {

    private SignInRequest validRequest;
    private Member member;

    @BeforeEach
    void setupMember() {
      final String loginId = "loginId";
      final String rawPassword = "password123";
      member = memberTestHelper.builder()
          .loginId(LoginId.from(loginId))
          .password(Password.from(rawPassword))
          .build();
      validRequest = SignInRequest.builder()
          .loginId(loginId)
          .rawPassword(rawPassword)
          .build();
    }

    @Test
    @DisplayName("????????? ???????????? ???????????? ???????????? ??????.")
    void should_successSignIn_when_validRequest() throws Exception {
      mockMvc.perform(post("/sign-in")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(validRequest)))
          .andExpect(status().isNoContent())
          .andExpect(cookie().exists(ACCESS_TOKEN.getTokenName()))
          .andExpect(cookie().exists(REFRESH_TOKEN.getTokenName()))
          .andDo(document("sign-in",
              requestFields(
                  fieldWithPath("loginId").description("????????? ?????????"),
                  fieldWithPath("password").description("????????????")
              ),
              responseCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName()).description("ACCESS TOKEN"),
                  cookieWithName(REFRESH_TOKEN.getTokenName()).description("REFRESH TOKEN")
              )
          ));
    }
  }

  @Nested
  @DisplayName("?????? ?????????")
  class Find {

    private Member member;

    @BeforeEach
    void setupMember() {
      member = memberTestHelper.generate();
    }

    @Test
    @DisplayName("????????? ??????????????? ????????? ???????????? ???????????? ??????.")
    void should_successfullySendLoginId_when_validEmail() throws Exception {
      doNothing().when(mailUtil).sendMail(anyList(), anyString(), anyString());
      FindLoginIdRequest request = FindLoginIdRequest.from(
          member.getProfile().getEmailAddress().get());

      mockMvc.perform(post("/sign-in/find-login-id")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andDo(print())
          .andExpect(status().isNoContent())
          .andDo(document("find-login-id",
              requestFields(
                  fieldWithPath("email").description("?????????")
              )));
    }

    @Test
    @DisplayName("????????? ???????????? ????????? ???????????? ?????? ?????? ??????????????? ???????????? ??????.")
    void should_successfullySendTmpPassword_when_validRequest() throws Exception {
      doNothing().when(signInService)
          .issueTemporaryPassword(any(EmailAddress.class), any(LoginId.class));
      IssueTmpPasswordRequest request = IssueTmpPasswordRequest.builder()
          .email(member.getProfile().getEmailAddress().get())
          .loginId(member.getProfile().getLoginId().get())
          .build();

      mockMvc.perform(patch("/sign-in/issue-tmp-password")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andDo(print())
          .andExpect(status().isNoContent())
          .andDo(document("issue-tmp-password",
              requestFields(
                  fieldWithPath("email").description("?????????"),
                  fieldWithPath("loginId").description("????????? ?????????")
              )));
    }
  }
}
