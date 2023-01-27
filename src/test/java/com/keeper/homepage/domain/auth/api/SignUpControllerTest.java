package com.keeper.homepage.domain.auth.api;

import static com.keeper.homepage.domain.auth.application.EmailAuthService.AUTH_CODE_LENGTH;
import static com.keeper.homepage.domain.auth.application.EmailAuthService.EMAIL_EXPIRED_SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.auth.dto.request.EmailAuthRequest;
import com.keeper.homepage.domain.auth.dto.request.SignUpRequest;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
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
      doReturn(EMAIL_EXPIRED_SECONDS).when(emailAuthService).emailAuth(any());
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
      doReturn(EMAIL_EXPIRED_SECONDS).when(emailAuthService).emailAuth(any());
      callEmailAuthApi(request)
          .andExpect(status().isBadRequest());
    }

    private ResultActions callEmailAuthApi(EmailAuthRequest request) throws Exception {
      return mockMvc.perform(post("/sign-up/email-auth")
          .contentType(APPLICATION_JSON)
          .content(asJsonString(request)));
    }
  }

  @Nested
  @DisplayName("회원가입 테스트")
  class SignUp {

    private final SignUpRequest validRequest = SignUpRequest.builder()
        .loginId("loginId_1337")
        .email("keeper@keeper.or.kr")
        .password("password123!@#$")
        .realName("정현모minion")
        .nickname("0v0zㅣ존")
        .authCode("0123456789")
        .birthday(LocalDate.of(1970, 1, 1))
        .studentId("197012345")
        .build();

    @Test
    @DisplayName("유효한 요청일 경우 회원가입은 성공해야 한다.")
    void should_successfully_when_validRequest() throws Exception {
      long createdMemberId = 1L;
      when(signUpService.signUp(any())).thenReturn(createdMemberId);
      callSignUpApi(validRequest)
          .andExpect(status().isCreated())
          .andExpect(header().string(HttpHeaders.LOCATION, "/members/" + createdMemberId))
          .andDo(document("sign-up",
              requestFields(
                  fieldWithPath("loginId").description("로그인 아이디는 4~12자 영어, 숫자, '_'만 가능합니다."),
                  fieldWithPath("email").description("이메일은 이메일 형식을 따라야 합니다."),
                  fieldWithPath("password").description("비밀번호는 8~20자여야 하고 영어, 숫자가 포함되어야 합니다."),
                  fieldWithPath("realName").description("실명은 1~20자 한글, 영어만 가능합니다."),
                  fieldWithPath("nickname").description("닉네임은 1~16자 한글, 영어, 숫자만 가능합니다."),
                  fieldWithPath("authCode")
                      .description("길이가 " + AUTH_CODE_LENGTH + "인 인증 코드를 입력해야 합니다."),
                  fieldWithPath("birthday").description("생일 형식은 yyyy.MM.dd 입니다.")
                      .optional(),
                  fieldWithPath("studentId").description("학번은 숫자만 가능합니다.")
              ),
              responseHeaders(
                  headerWithName(HttpHeaders.LOCATION).description("생성된 회원의 URI입니다.")
              )));
    }

    private ResultActions callSignUpApi(SignUpRequest request) throws Exception {
      return mockMvc.perform(post("/sign-up")
          .contentType(APPLICATION_JSON_VALUE)
          .content(asJsonString(request)));
    }
  }
}
