package com.keeper.homepage.domain.auth.application;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.type.MemberType.MemberTypeEnum.가입대기;
import static com.keeper.homepage.domain.member.entity.type.MemberType.MemberTypeEnum.정회원;
import static com.keeper.homepage.domain.member.entity.type.MemberType.getMemberTypeBy;
import static com.keeper.homepage.global.config.security.session.SessionPolicy.SESSION_COOKIE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.domain.auth.api.SignInController;
import com.keeper.homepage.domain.auth.dao.redis.EmailAuthRedisRepository;
import com.keeper.homepage.domain.auth.entity.redis.EmailAuthRedis;
import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.EmailAddress;
import com.keeper.homepage.domain.member.entity.embedded.LoginId;
import com.keeper.homepage.domain.member.entity.embedded.Password;
import com.keeper.homepage.domain.member.entity.embedded.Profile;
import com.keeper.homepage.domain.member.entity.embedded.RealName;
import com.keeper.homepage.domain.member.entity.embedded.StudentId;
import com.keeper.homepage.domain.member.entity.type.MemberType.MemberTypeEnum;
import com.keeper.homepage.global.config.security.session.SessionCreationResult;
import com.keeper.homepage.global.config.security.session.SessionData;
import com.keeper.homepage.global.error.ExceptionAdvice;
import com.keeper.homepage.global.util.mail.MailUtil;
import com.keeper.homepage.global.util.redis.RedisUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PendingMemberAuthenticationTest {

  private static final String RAW_PASSWORD = "password123";
  private static final long MEMBER_ID = 123L;

  private final MemberRepository memberRepository = mock(MemberRepository.class);
  private final SessionService sessionService = mock(SessionService.class);
  private final AuthCookieService authCookieService = spy(new AuthCookieService());
  private final SignInService signInService = new SignInService(memberRepository,
      authCookieService, sessionService, mock(RedisUtil.class), mock(MailUtil.class));
  private final MockMvc mockMvc = MockMvcBuilders
      .standaloneSetup(new SignInController(signInService))
      .setControllerAdvice(new ExceptionAdvice())
      .build();

  private Member member;

  @BeforeEach
  void setUp() {
    Profile profile = Profile.builder()
        .loginId(LoginId.from("loginId"))
        .emailAddress(EmailAddress.from("keeper@keeper.or.kr"))
        .password(Password.from(RAW_PASSWORD))
        .realName(RealName.from("테스트"))
        .studentId(StudentId.from("202012345"))
        .build();
    member = Member.builder()
        .profile(profile)
        .point(0)
        .level(0)
        .totalAttendance(0)
        .build();
    ReflectionTestUtils.setField(member, "id", MEMBER_ID);
    when(memberRepository.findByProfileLoginId(profile.getLoginId()))
        .thenReturn(Optional.of(member));
  }

  @Test
  void signUpStoresPendingMemberWithMemberRoleAndEncryptedPassword() {
    var emailAuthRepository = mock(EmailAuthRedisRepository.class);
    var signUpService = new SignUpService(memberRepository, emailAuthRepository,
        new CheckDuplicateService(memberRepository));
    String email = member.getProfile().getEmailAddress().get();
    String authCode = "0123456789";
    when(emailAuthRepository.findById(email))
        .thenReturn(Optional.of(EmailAuthRedis.of(email, authCode)));
    when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
      Member savedMember = invocation.getArgument(0);
      ReflectionTestUtils.setField(savedMember, "id", MEMBER_ID);
      return savedMember;
    });

    long memberId = signUpService.signUp(member.getProfile(), authCode);

    var captor = ArgumentCaptor.forClass(Member.class);
    verify(memberRepository).save(captor.capture());
    Member savedMember = captor.getValue();
    assertThat(memberId).isEqualTo(MEMBER_ID);
    assertThat(savedMember.isType(가입대기)).isTrue();
    assertThat(savedMember.getMemberType().getId()).isEqualTo(5L);
    assertThat(savedMember.getJobs()).containsExactly(ROLE_회원.name());
    assertThat(savedMember.getProfile().getPassword().get()).isNotEqualTo(RAW_PASSWORD);
    assertThat(savedMember.getProfile().getPassword().isWrongPassword(RAW_PASSWORD)).isFalse();
  }

  @Test
  void pendingMemberCannotSignInOrCreateSession() throws Exception {
    member.updateType(getMemberTypeBy(가입대기));

    signIn(RAW_PASSWORD)
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message")
            .value("[memberType] 가입대기: 가입 승인 대기 중입니다."))
        .andExpect(cookie().doesNotExist(SESSION_COOKIE_NAME));

    verifyNoInteractions(sessionService, authCookieService);
  }

  @Test
  void wrongPasswordTakesPrecedenceOverPendingApproval() throws Exception {
    member.updateType(getMemberTypeBy(가입대기));

    signIn("wrongPassword123")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message")
            .value("[loginId] loginId: 아이디 혹은 비밀번호가 잘못되었습니다."))
        .andExpect(cookie().doesNotExist(SESSION_COOKIE_NAME));

    verifyNoInteractions(sessionService, authCookieService);
  }

  @Test
  void unknownMemberKeepsExistingError() throws Exception {
    when(memberRepository.findByProfileLoginId(member.getProfile().getLoginId()))
        .thenReturn(Optional.empty());

    signIn(RAW_PASSWORD)
        .andExpect(status().isNotFound())
        .andExpect(cookie().doesNotExist(SESSION_COOKIE_NAME));

    verifyNoInteractions(sessionService, authCookieService);
  }

  @ParameterizedTest
  @EnumSource(value = MemberTypeEnum.class, names = "가입대기", mode = EnumSource.Mode.EXCLUDE)
  void existingMemberTypesCanStillSignIn(MemberTypeEnum type) throws Exception {
    member.updateType(getMemberTypeBy(type));
    stubSessionCreation();

    signIn(RAW_PASSWORD)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.memberType").value(type.name()))
        .andExpect(jsonPath("$.memberJobs[0]").value(ROLE_회원.name()))
        .andExpect(cookie().value(SESSION_COOKIE_NAME, "approved-session"));

    verify(sessionService).createSession(MEMBER_ID, List.of(ROLE_회원.name()));
  }

  @Test
  void changingPendingMemberToRegularAllowsSignInWithExistingRole() throws Exception {
    member.updateType(getMemberTypeBy(가입대기));
    signIn(RAW_PASSWORD).andExpect(status().isForbidden());
    verifyNoInteractions(sessionService, authCookieService);

    member.updateType(getMemberTypeBy(정회원));
    stubSessionCreation();

    signIn(RAW_PASSWORD)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.memberType").value("정회원"))
        .andExpect(jsonPath("$.memberJobs[0]").value(ROLE_회원.name()))
        .andExpect(cookie().value(SESSION_COOKIE_NAME, "approved-session"));
  }

  private void stubSessionCreation() {
    List<String> roles = List.of(ROLE_회원.name());
    when(sessionService.createSession(MEMBER_ID, roles))
        .thenReturn(new SessionCreationResult("approved-session",
            new SessionData(MEMBER_ID, 0, 60_000, roles), 60_000));
  }

  private ResultActions signIn(String password) throws Exception {
    return mockMvc.perform(post("/sign-in")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"loginId": "loginId", "password": "%s"}
            """.formatted(password)));
  }
}
