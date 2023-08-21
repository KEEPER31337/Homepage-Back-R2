package com.keeper.homepage.domain.auth.api;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.config.security.data.JwtType.REFRESH_TOKEN;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.auth.dto.request.ChangePasswordForMissingRequest;
import com.keeper.homepage.domain.auth.dto.request.FindLoginIdRequest;
import com.keeper.homepage.domain.auth.dto.request.MemberIdAndEmailRequest;
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
import org.springframework.util.LinkedMultiValueMap;

class SignInControllerTest extends IntegrationTest {

  @Nested
  @DisplayName("로그인 테스트")
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
    @DisplayName("유효한 요청이면 로그인에 성공해야 한다.")
    void should_successSignIn_when_validRequest() throws Exception {
      mockMvc.perform(post("/sign-in")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(validRequest)))
          .andExpect(status().isOk())
          .andExpect(cookie().exists(ACCESS_TOKEN.getTokenName()))
          .andExpect(cookie().exists(REFRESH_TOKEN.getTokenName()))
          .andDo(document("sign-in",
              requestFields(
                  fieldWithPath("loginId").description("로그인 아이디"),
                  fieldWithPath("password").description("비밀번호")
              ),
              responseFields(
                  fieldWithPath("memberId").description("회원 아이디"),
                  fieldWithPath("loginId").description("로그인 아이디"),
                  fieldWithPath("emailAddress").description("이메일"),
                  fieldWithPath("realName").description("실명"),
                  fieldWithPath("birthday").description("생일"),
                  fieldWithPath("studentId").description("학번"),
                  fieldWithPath("thumbnailPath").description("썸네일 경로"),
                  fieldWithPath("generation").description("기수"),
                  fieldWithPath("point").description("보유 포인트"),
                  fieldWithPath("level").description("레벨"),
                  fieldWithPath("totalAttendance").description("총 출석 횟수"),
                  fieldWithPath("memberType").description("회원 타입"),
                  fieldWithPath("memberRank").description("회원 랭크"),
                  fieldWithPath("memberJobs[]").description("회원 역할")
              ),
              responseCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName()).description("ACCESS TOKEN"),
                  cookieWithName(REFRESH_TOKEN.getTokenName()).description("REFRESH TOKEN")
              )
          ));
    }
  }

  @Nested
  @DisplayName("찾기 테스트")
  class Find {

    private Member member;

    @BeforeEach
    void setupMember() {
      member = memberTestHelper.generate();
    }

    @Test
    @DisplayName("유효한 이메일이면 로그인 아이디가 전송되야 한다.")
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
                  fieldWithPath("email").description("이메일")
              )));
    }

    @Test
    @DisplayName("유효한 이메일과 로그인 아이디일 경우 비밀번호 인증코드가 전송되야 한다.")
    void should_successfullySendTmpPassword_when_validRequest() throws Exception {
      doNothing().when(signInService)
          .sendPasswordChangeAuthCode(any(EmailAddress.class), any(LoginId.class));
      MemberIdAndEmailRequest request = MemberIdAndEmailRequest.builder()
          .email(member.getProfile().getEmailAddress().get())
          .loginId(member.getProfile().getLoginId().get())
          .build();

      mockMvc.perform(post("/sign-in/send-password-change-auth-code")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andDo(print())
          .andExpect(status().isNoContent())
          .andDo(document("send-password-change-auth-code",
              requestFields(
                  fieldWithPath("email").description("이메일"),
                  fieldWithPath("loginId").description("로그인 아이디")
              )));
    }

    @Test
    @DisplayName("유효한 인증코드일 경우 true를 반환한다.")
    void should_returnTrue_when_validAuthCode() throws Exception {
      signInService.sendPasswordChangeAuthCode(member.getProfile().getEmailAddress(),
          member.getProfile().getLoginId());
      String authCode = redisUtil.getData("PW_AUTH_" + member.getId(), String.class).orElseThrow();
      var params = new LinkedMultiValueMap<String, String>();
      params.add("email", member.getProfile().getEmailAddress().get());
      params.add("loginId", member.getProfile().getLoginId().get());
      params.add("authCode", authCode);

      mockMvc.perform(get("/sign-in/check-auth-code")
              .params(params))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.auth").value(true))
          .andDo(document("check-auth-code",
              queryParameters(
                  parameterWithName("email").description("이메일"),
                  parameterWithName("loginId").description("로그인 아이디"),
                  parameterWithName("authCode").description("인증 코드")
              ),
              responseFields(
                  fieldWithPath("auth").description("authCode가 일치하면 true, 아니면 false")
              )));
    }

    @Test
    @DisplayName("인증코드가 일치하면 비밀번호 변경은 성공한다")
    void should_changePassword_when_validAuthCode() throws Exception {
      signInService.sendPasswordChangeAuthCode(member.getProfile().getEmailAddress(),
          member.getProfile().getLoginId());
      String authCode = redisUtil.getData("PW_AUTH_" + member.getId(), String.class).orElseThrow();
      String newPassword = "newPassword123";

      mockMvc.perform(patch("/sign-in/change-password-for-missing")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(ChangePasswordForMissingRequest.builder()
                  .loginId(member.getProfile().getLoginId().get())
                  .email(member.getProfile().getEmailAddress().get())
                  .authCode(authCode)
                  .rawPassword(newPassword)
                  .build())))
          .andDo(print())
          .andExpect(status().isNoContent())
          .andDo(document("change-password-for-missing",
              requestFields(
                  fieldWithPath("email").description("이메일"),
                  fieldWithPath("loginId").description("로그인 아이디"),
                  fieldWithPath("authCode").description("인증 코드"),
                  fieldWithPath("password").description("새로운 비밀번호")
              )));

      assertThat(member.getProfile().getPassword().isWrongPassword(newPassword)).isFalse();
    }
  }
}
