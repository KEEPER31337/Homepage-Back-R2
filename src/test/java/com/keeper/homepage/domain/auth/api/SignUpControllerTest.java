package com.keeper.homepage.domain.auth.api;

import static com.keeper.homepage.domain.auth.application.EmailAuthService.AUTH_CODE_LENGTH;
import static com.keeper.homepage.domain.auth.application.EmailAuthService.EMAIL_EXPIRED_SECONDS;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.auth.dto.request.EmailAuthRequest;
import com.keeper.homepage.domain.auth.dto.request.SignUpRequest;
import com.keeper.homepage.domain.member.entity.Member;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;

class SignUpControllerTest extends IntegrationTest {

  @Nested
  @DisplayName("이메일 인증 테스트")
  class EmailAddressAuth {

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
        .rawPassword("password123!@#$")
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
      doReturn(createdMemberId).when(signUpService).signUp(any(), any());
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

    @ParameterizedTest
    @MethodSource
    @DisplayName("잘못된 형식의 요청일 경우 400 Bad Request를 반환해야 한다.")
    void should_400BadRequest_when_invalidRequest(String field, Object invalidValue)
        throws Exception {
      Object validValue = ReflectionTestUtils.getField(validRequest, field);
      ReflectionTestUtils.setField(validRequest, field, invalidValue);
      callSignUpApi(validRequest)
          .andExpect(status().isBadRequest())
          .andExpect(content().string(containsString(field)))
          .andExpect(content().string(containsString(invalidValue.toString())));
      ReflectionTestUtils.setField(validRequest, field, validValue);
    }

    static Stream<Arguments> should_400BadRequest_when_invalidRequest() {
      return Stream.of(
          Arguments.arguments("loginId", "a_0"),
          Arguments.arguments("loginId", "a".repeat(13)),
          Arguments.arguments("loginId", "abcd#"),
          Arguments.arguments("loginId", "no-dash-haha"),
          Arguments.arguments("email", "a@a."),
          Arguments.arguments("email", "notEmail"),
          Arguments.arguments("rawPassword", "a".repeat(6) + "0"),
          Arguments.arguments("rawPassword", "a".repeat(20) + "0"),
          Arguments.arguments("rawPassword", "abcdefghij"),
          Arguments.arguments("rawPassword", "0123456789"),
          Arguments.arguments("rawPassword", "noNumber###"),
          Arguments.arguments("rawPassword", "0123456!@#$"),
          Arguments.arguments("realName", "a".repeat(21)),
          Arguments.arguments("realName", ""),
          Arguments.arguments("realName", "  "),
          Arguments.arguments("realName", "noNumber00"),
          Arguments.arguments("nickname", "0_0"),
          Arguments.arguments("nickname", "0-0"),
          Arguments.arguments("nickname", "noSpecial!@#$"),
          Arguments.arguments("nickname", "공백은 안됩니다."),
          Arguments.arguments("authCode", "a".repeat(AUTH_CODE_LENGTH - 1)),
          Arguments.arguments("authCode", "a".repeat(AUTH_CODE_LENGTH + 1)),
          Arguments.arguments("studentId", "noNumber!"),
          Arguments.arguments("studentId", "12345_6789")
      );
    }

    private ResultActions callSignUpApi(SignUpRequest request) throws Exception {
      return mockMvc.perform(post("/sign-up")
          .contentType(APPLICATION_JSON_VALUE)
          .content(asJsonString(request)));
    }
  }

  @Nested
  @DisplayName("중복 확인 테스트")
  class CheckDuplicate {

    Member member;

    @BeforeEach
    void setup() {
      member = memberTestHelper.generate();
    }

    @Test
    @DisplayName("이미 존재하는 로그인 아이디일 경우 true를 반환해야 한다.")
    void should_returnTrue_when_existsLoginId() throws Exception {
      callCheckDuplicateApi(Field.LOGIN_ID, member.getProfile().getLoginId().get())
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.duplicate").value(true))
          .andDo(document("check-duplicate-loginId",
              queryParameters(
                  parameterWithName("loginId").description("중복을 확인할 로그인 아이디를 넣어주세요.")
              ),
              responseFields(
                  fieldWithPath("duplicate").description("중복이면 true, 아니면 false")
              )));
    }

    @Test
    @DisplayName("이미 존재하는 이메일일 경우 true를 반환해야 한다.")
    void should_returnTrue_when_existsEmail() throws Exception {
      callCheckDuplicateApi(Field.EMAIL, member.getProfile().getEmailAddress().get())
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.duplicate").value(true))
          .andDo(document("check-duplicate-email",
              queryParameters(
                  parameterWithName("email").description("중복을 확인할 이메일을 넣어주세요.")
              ),
              responseFields(
                  fieldWithPath("duplicate").description("중복이면 true, 아니면 false")
              )));
    }

    @Test
    @DisplayName("이미 존재하는 학번일 경우 true를 반환해야 한다.")
    void should_returnTrue_when_exigetStudentId() throws Exception {
      callCheckDuplicateApi(Field.STUDENT_ID, member.getProfile().getStudentId().get())
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.duplicate").value(true))
          .andDo(document("check-duplicate-studentId",
              queryParameters(
                  parameterWithName("studentId").description("중복을 확인할 학번을 넣어주세요.")
              ),
              responseFields(
                  fieldWithPath("duplicate").description("중복이면 true, 아니면 false")
              )));
    }

    private ResultActions callCheckDuplicateApi(Field field, String value) throws Exception {
      return mockMvc.perform(get("/sign-up/exists" + field.url)
          .param(field.param, value));
    }

    private enum Field {

      LOGIN_ID("/login-id", "loginId"),
      EMAIL("/email", "email"),
      STUDENT_ID("/student-id", "studentId"),
      ;

      private final String url;
      private final String param;

      Field(String url, String param) {
        this.url = url;
        this.param = param;
      }
    }
  }
}
