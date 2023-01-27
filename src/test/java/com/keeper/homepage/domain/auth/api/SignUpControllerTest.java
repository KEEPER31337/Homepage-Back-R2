package com.keeper.homepage.domain.auth.api;

import static com.keeper.homepage.domain.auth.application.EmailAuthService.EMAIL_EXPIRED_SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.auth.dto.request.EmailAuthRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.ResultActions;

class SignUpControllerTest extends IntegrationTest {

  @Nested
  @DisplayName("이메일 인증 테스트")
  class EmailAuth {

    private static final String VALID_EMAIL = "email@email.com";

    @Test
    @DisplayName("유효한 요청이면 이메일 인증은 성공해야 한다.")
    void should_successfully_when_validRequest() throws Exception {
      EmailAuthRequest request = EmailAuthRequest.from(VALID_EMAIL);
      when(emailAuthService.emailAuth(any())).thenReturn(EMAIL_EXPIRED_SECONDS);
      callEmailAuthApi(request)
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.expiredSeconds").value(EMAIL_EXPIRED_SECONDS))
          .andDo(document("email-auth",
              requestFields(
                  fieldWithPath("email").description("인증을 보낼 이메일을 보내시면 됩니다.")
              ),
              responseFields(
                  fieldWithPath("expiredSeconds").description("인증코드 유효 기간 입니다. (단위: 초)")
              )));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a@a.", "notEmail", "ho@hoo.hoh@oho", "isEmail@nono@com"})
    @DisplayName("이메일 형식에 맞지 않으면 400 Bad Request를 응답한다.")
    void should_throwException_when_invalidRequest(String invalidEmail) throws Exception {
      EmailAuthRequest request = EmailAuthRequest.from(invalidEmail);
      when(emailAuthService.emailAuth(any())).thenReturn(EMAIL_EXPIRED_SECONDS);
      callEmailAuthApi(request)
          .andExpect(status().isBadRequest());
    }

    private ResultActions callEmailAuthApi(EmailAuthRequest request) throws Exception {
      return mockMvc.perform(post("/sign-up/email-auth")
          .contentType(APPLICATION_JSON)
          .content(asJsonString(request)));
    }
  }
}
