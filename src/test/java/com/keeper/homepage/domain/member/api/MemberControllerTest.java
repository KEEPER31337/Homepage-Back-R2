package com.keeper.homepage.domain.member.api;

import static com.keeper.homepage.domain.member.entity.embedded.RealName.REAL_NAME_INVALID;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_사서;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static com.keeper.homepage.domain.member.entity.type.MemberType.MemberTypeEnum.휴면회원;
import static com.keeper.homepage.domain.member.entity.type.MemberType.getMemberTypeBy;
import static com.keeper.homepage.global.config.security.session.SessionPolicy.SESSION_COOKIE_NAME;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.pageHelper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.domain.auth.dto.request.EmailAuthRequest;
import com.keeper.homepage.domain.member.dto.request.AdminDeleteMemberRequest;
import com.keeper.homepage.domain.member.dto.request.ChangePasswordRequest;
import com.keeper.homepage.domain.member.dto.request.DeleteMemberRequest;
import com.keeper.homepage.domain.member.dto.request.ProfileUpdateRequest;
import com.keeper.homepage.domain.member.dto.request.UpdateMemberEmailAddressRequest;
import com.keeper.homepage.domain.member.dto.request.UpdateMemberTypeRequest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.EmailAddress;
import com.keeper.homepage.domain.member.entity.embedded.LoginId;
import com.keeper.homepage.domain.member.entity.embedded.Password;
import com.keeper.homepage.domain.member.entity.embedded.Profile;
import com.keeper.homepage.domain.member.entity.embedded.RealName;
import com.keeper.homepage.domain.member.entity.embedded.StudentId;
import com.keeper.homepage.global.config.security.session.SessionData;
import com.keeper.homepage.global.config.security.session.SessionIdCodec;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.snippet.Attributes.Attribute;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class MemberControllerTest extends MemberApiTestHelper {

  @Nested
  @DisplayName("로그인 회원 정보 조회")
  class GetMyProfile {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private Member member;
    private Cookie[] sessionCookies;

    @BeforeEach
    void setup() {
      member = memberTestHelper.builder()
          .loginId(LoginId.from("myprofileuser"))
          .emailAddress(EmailAddress.from("me@example.com"))
          .realName(RealName.from("홍길동"))
          .birthday(LocalDate.of(2000, 1, 2))
          .studentId(StudentId.from("202612345"))
          .point(1200)
          .level(3)
          .totalAttendance(42)
          .build();
      member.assignJob(ROLE_회장);
      sessionCookies = memberTestHelper.getSessionCookies(member);
      em.flush();
      em.clear();
    }

    @Test
    @DisplayName("세션으로 인증된 본인의 상세 정보를 반환한다.")
    void returnsAuthenticatedMemberDetails() throws Exception {
      mockMvc.perform(get("/members/me").cookie(sessionCookies))
          .andExpect(status().isOk())
          .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
          .andExpect(jsonPath("$.memberId").value(member.getId()))
          .andExpect(jsonPath("$.loginId").value("myprofileuser"))
          .andExpect(jsonPath("$.emailAddress").value("me@example.com"))
          .andExpect(jsonPath("$.realName").value("홍길동"))
          .andExpect(jsonPath("$.birthday").value("2000-01-02"))
          .andExpect(jsonPath("$.studentId").value("202612345"))
          .andExpect(jsonPath("$.thumbnailPath").hasJsonPath())
          .andExpect(jsonPath("$.thumbnailPath").value(nullValue()))
          .andExpect(jsonPath("$.generation").value(member.getGeneration()))
          .andExpect(jsonPath("$.point").value(1200))
          .andExpect(jsonPath("$.level").value(3))
          .andExpect(jsonPath("$.totalAttendance").value(42))
          .andExpect(jsonPath("$.memberType").value("정회원"))
          .andExpect(jsonPath("$.memberRank").value("일반회원"))
          .andExpect(jsonPath("$.memberJobs", containsInAnyOrder("ROLE_회원", "ROLE_회장")))
          .andDo(document("get-my-profile",
              requestCookies(cookieWithName(SESSION_COOKIE_NAME).description("OPAQUE SESSION ID")),
              responseFields(getMyProfileResponse())));
    }

    @Test
    @DisplayName("다른 회원의 세션으로 요청하면 해당 회원의 정보를 반환한다.")
    void identifiesMemberFromSession() throws Exception {
      Member other = memberTestHelper.generate();
      Cookie[] otherCookies = memberTestHelper.getSessionCookies(other);
      em.flush();
      em.clear();

      mockMvc.perform(get("/members/me").cookie(otherCookies))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.memberId").value(other.getId()))
          .andExpect(jsonPath("$.loginId").value(other.getProfile().getLoginId().get()))
          .andExpect(jsonPath("$.studentId").value(other.getProfile().getStudentId().get()));
    }

    @Test
    @DisplayName("생일이 미등록 상태이면 필드를 생략하지 않고 null을 반환한다.")
    void returnsNullBirthday() throws Exception {
      Member savedMember = memberRepository.findById(member.getId()).orElseThrow();
      savedMember.getProfile().update(Profile.builder()
          .realName(savedMember.getProfile().getRealName())
          .birthday(null)
          .build());
      em.flush();
      em.clear();

      mockMvc.perform(get("/members/me").cookie(sessionCookies))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.birthday").hasJsonPath())
          .andExpect(jsonPath("$.birthday").value(nullValue()));
    }

    @Test
    @DisplayName("로그인 이후 변경된 프로필과 역할은 재조회 시 DB의 최신 값을 반환한다.")
    void returnsCurrentMemberDetails() throws Exception {
      mockMvc.perform(get("/members/me").cookie(sessionCookies))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.memberJobs", containsInAnyOrder("ROLE_회원", "ROLE_회장")));

      Member savedMember = memberRepository.findById(member.getId()).orElseThrow();
      savedMember.getProfile().update(Profile.builder()
          .realName(RealName.from("김키퍼"))
          .birthday(LocalDate.of(2001, 3, 4))
          .build());
      savedMember.getProfile().updateEmailAddress("updated@example.com");
      var thumbnail = thumbnailTestHelper.generateThumbnail();
      savedMember.getProfile().updateThumbnail(thumbnail);
      savedMember.deleteJob(ROLE_회장);
      savedMember.assignJob(ROLE_사서);
      savedMember.updateType(getMemberTypeBy(휴면회원));
      memberRepository.updatePointByDelta(member.getId(), 100, Integer.MAX_VALUE);
      em.flush();
      em.clear();

      mockMvc.perform(get("/members/me").cookie(sessionCookies))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.memberId").value(member.getId()))
          .andExpect(jsonPath("$.realName").value("김키퍼"))
          .andExpect(jsonPath("$.birthday").value("2001-03-04"))
          .andExpect(jsonPath("$.emailAddress").value("updated@example.com"))
          .andExpect(jsonPath("$.thumbnailPath").value(thumbnail.getPath()))
          .andExpect(jsonPath("$.point").value(1300))
          .andExpect(jsonPath("$.memberType").value("휴면회원"))
          .andExpect(jsonPath("$.memberJobs", containsInAnyOrder("ROLE_회원", "ROLE_사서")));
    }

    @Test
    @DisplayName("세션 쿠키가 없으면 401을 반환한다.")
    void unauthorizedWithoutSession() throws Exception {
      mockMvc.perform(get("/members/me"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("무효한 세션 쿠키이면 401을 반환한다.")
    void unauthorizedWithInvalidSession() throws Exception {
      mockMvc.perform(get("/members/me")
              .cookie(new Cookie(SESSION_COOKIE_NAME, "invalid-session")))
          .andExpect(status().isUnauthorized());

      sessionService.deleteSession(sessionCookies[0].getValue());
      mockMvc.perform(get("/members/me").cookie(sessionCookies))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("세션이 만료되면 401을 반환한다.")
    void unauthorizedWithExpiredSession() throws Exception {
      String key = new SessionIdCodec().toRedisKey(sessionCookies[0].getValue()).orElseThrow();
      SessionData expired = new SessionData(member.getId(), 0, 0, List.of(ROLE_회원.name()));
      stringRedisTemplate.opsForValue().set(key, asJsonString(expired), Duration.ofDays(1));

      mockMvc.perform(get("/members/me").cookie(sessionCookies))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("세션이 가리키는 회원이 삭제되었으면 기존 방식대로 404를 반환한다.")
    void notFoundWhenMemberWasDeleted() throws Exception {
      memberRepository.deleteById(member.getId());
      em.flush();
      em.clear();

      mockMvc.perform(get("/members/me").cookie(sessionCookies))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  class ChangePassword {

    private final String oldPassword = "oldPassword123!";
    private Member member;
    private Cookie[] sessionCookies;

    @BeforeEach
    void setup() {
      member = memberTestHelper.builder()
          .password(Password.from(oldPassword))
          .build();
      sessionCookies = memberTestHelper.getSessionCookies(member);
    }

    @Test
    @DisplayName("유효한 비밀번호로 변경을 요청했을 때 204 NO CONTENT를 반환해야 한다.")
    void should_responseNoContent_when_validPassword() throws Exception {
      ChangePasswordRequest request = ChangePasswordRequest.from(oldPassword, "password123!@#");

      mockMvc.perform(patch("/members/change-password")
              .cookie(sessionCookies)
              .contentType(APPLICATION_JSON)
              .content(asJsonString(request)))
          .andExpect(status().isNoContent())
          .andExpect(header().string(HttpHeaders.LOCATION, "/members/me"))
          .andDo(document("change-password",
              requestCookies(
                  cookieWithName(SESSION_COOKIE_NAME).description("OPAQUE SESSION ID")),
              requestFields(
                  fieldWithPath("oldPassword").description("현재 패스워드"),
                  fieldWithPath("newPassword").description("새로운 패스워드")),
              responseHeaders(
                  headerWithName(HttpHeaders.LOCATION).description("비밀번호 변경한 리소스의 위치입니다."))));
    }

    @Test
    @DisplayName("원래 비밀번호가 아니면 400 Bad Request를 반환해야 한다.")
    void should_responseBadRequest_when_wrongPassword() throws Exception {
      ChangePasswordRequest request = ChangePasswordRequest.from("wrongPassword", "password123!@#");

      mockMvc.perform(patch("/members/change-password")
              .cookie(sessionCookies)
              .contentType(APPLICATION_JSON)
              .content(asJsonString(request)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("회원 목록 조회")
  class GetMembers {

    private Member member;
    private String memberSessionId;

    @BeforeEach
    void setUp() throws IOException {
      member = memberTestHelper.builder().build();
      memberSessionId = sessionService.createSessionId(member.getId(), ROLE_회원);
    }

    @Test
    @DisplayName("유효한 요청일 경우 회원 목록 조회(검색)는 성공한다.")
    public void 유효한_요청일_경우_회원_목록_조회는_성공한다() throws Exception {
      assertThat(member.getGeneration()).isNotNull();
      String securedValue = getSecuredValue(MemberController.class, "getMembersByRealName");

      mockMvc.perform(get("/members/real-name")
              .cookie(new Cookie(SESSION_COOKIE_NAME, memberSessionId))
              .contentType(APPLICATION_JSON))
          .andExpect(status().isOk())
          .andDo(document("get-members-by-real-name",
              requestCookies(
                  cookieWithName(SESSION_COOKIE_NAME)
                      .description("OPAQUE SESSION ID %s".formatted(securedValue))
              ),
              queryParameters(
                  parameterWithName("searchName").description("회원 이름 검색어")
                      .attributes(new Attribute("format", "null : 전체 목록 조회"))
                      .optional()
              ),
              responseFields(
                  fieldWithPath("[].memberId").description("회원 ID"),
                  fieldWithPath("[].realName").description("회원 실명"),
                  fieldWithPath("[].generation").description("회원 기수"),
                  fieldWithPath("[].memberType").description("회원 타입"),
                  fieldWithPath("[].thumbnailPath").description("회원 썸네일 주소")
              )));
    }
  }

  @Nested
  @DisplayName("누적 포인트 랭킹 테스트")
  class PointRanking {

    private String memberSessionId;
    private final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    @BeforeEach
    void setUp() throws IOException {
      long memberId;
      memberTestHelper.builder().point(0).build();
      memberTestHelper.builder().point(100).build();
      memberId = memberTestHelper.builder().point(1000).build().getId();
      memberSessionId = sessionService.createSessionId(memberId, ROLE_회원);
    }

    @Test
    @DisplayName("유효한 요청이면 누적 포인트 랭킹 조회는 성공해야 한다.")
    public void 유효한_요청이면_누적_포인트_랭킹_조회는_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(MemberController.class, "getPointRanks");

      params.add("page", "0");
      params.add("size", "3");
      callGetPointRankingApi(memberSessionId, params)
          .andExpect(status().isOk())
          .andDo(document("get-point-ranks",
              requestCookies(
                  cookieWithName(SESSION_COOKIE_NAME)
                      .description("OPAQUE SESSION ID %s".formatted(securedValue))
              ),
              queryParameters(
                  parameterWithName("page").description("페이지 (default: 0)")
                      .optional(),
                  parameterWithName("size").description("한 페이지당 불러올 개수 (default: 10)")
                      .optional()
              ),
              responseFields(
                  pageHelper(getPointRankResponse())
              )));
    }
  }

  @Nested
  @DisplayName("회원 팔로우 언팔로우 테스트")
  class FriendTest {

    private String memberSessionId;
    private long memberId;
    private long otherId;

    @BeforeEach
    void setUp() throws IOException {
      memberId = memberTestHelper.generate().getId();
      otherId = memberTestHelper.generate().getId();
      memberSessionId = sessionService.createSessionId(memberId, ROLE_회원);
    }

    @Test
    @DisplayName("유효한 요청일 경우 회원 팔로우는 성공한다.")
    public void 유효한_요청일_경우_회원_팔로우는_성공한다() throws Exception {
      String securedValue = getSecuredValue(MemberController.class, "follow");

      mockMvc.perform(post("/members/{memberId}/follow", otherId)
              .cookie(new Cookie(SESSION_COOKIE_NAME, memberSessionId)))
          .andExpect(status().isCreated())
          .andDo(document("follow-member",
              requestCookies(
                  cookieWithName(SESSION_COOKIE_NAME)
                      .description("OPAQUE SESSION ID %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("memberId").description("회원 ID")
              )));
    }

    @Test
    @DisplayName("유효한 요청일 경우 회원 언팔로우는 성공한다.")
    public void 유효한_요청일_경우_회원_언팔로우는_성공한다() throws Exception {
      String securedValue = getSecuredValue(MemberController.class, "unfollow");

      mockMvc.perform(delete("/members/{memberId}/unfollow", otherId)
              .cookie(new Cookie(SESSION_COOKIE_NAME, memberSessionId)))
          .andExpect(status().isNoContent())
          .andDo(document("unfollow-member",
              requestCookies(
                  cookieWithName(SESSION_COOKIE_NAME)
                      .description("OPAQUE SESSION ID %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("memberId").description("회원 ID")
              )));
    }
  }

  @Nested
  @DisplayName("프로필 수정")
  class UpdateProfile {

    private Member member;
    private String memberSessionId;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.generate();
      memberSessionId = sessionService.createSessionId(member.getId(), ROLE_회원);
    }

    @Test
    @DisplayName("유효한 요청일 경우 회원의 프로필 내용을 수정 한다.")
    public void should_success_myProfile() throws Exception {
      String securedValue = getSecuredValue(MemberController.class, "updateProfile");

      ProfileUpdateRequest request = ProfileUpdateRequest.builder()
          .realName("바뀐이름")
          .birthday(LocalDate.of(1970, 1, 1))
          .build();

      callUpdateProfileApi(memberSessionId, request)
          .andExpect(status().isNoContent())
          .andDo(document("update-profile",
              requestCookies(
                  cookieWithName(SESSION_COOKIE_NAME)
                      .description("OPAQUE SESSION ID %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("realName").description("실명을 입력해주세요. (" + REAL_NAME_INVALID + ")"),
                  fieldWithPath("birthday").description("생년월일을 입력해주세요.").optional()
              )));
    }
  }

  @Nested
  @DisplayName("회원 프로필 썸네일 변경 테스트")
  class MemberThumbnailTest {

    private Member member;
    private MockMultipartFile thumbnail;
    private String memberSessionId;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.generate();
      thumbnail = thumbnailTestHelper.getThumbnailFile();
      memberSessionId = sessionService.createSessionId(member.getId(), ROLE_회원);
    }

    @Test
    @DisplayName("유효한 요청일 경우 회원의 프로필이 수정되어야 한다.")
    void 유효한_요청일_경우_회원의_프로필이_수정되어야_한다() throws Exception {
      String securedValue = getSecuredValue(MemberController.class, "updateProfileThumbnail");
      mockMvc.perform(multipart("/members/thumbnail")
              .file(thumbnail)
              .with(request -> {
                request.setMethod("PATCH");
                return request;
              })
              .cookie(new Cookie(SESSION_COOKIE_NAME, memberSessionId))
              .contentType(MediaType.MULTIPART_FORM_DATA))
          .andExpect(status().isNoContent())
          .andDo(document("update-member-thumbnail",
              requestCookies(
                  cookieWithName(SESSION_COOKIE_NAME)
                      .description("OPAQUE SESSION ID %s".formatted(securedValue))
              ),
              requestParts(
                  partWithName("thumbnail").description("변경할 썸네일")
              )));
    }
  }

  @Nested
  @DisplayName("회원 프로필 조회")
  class getMemberProfile {

    private Member member, otherMember;
    private String memberSessionId, otherMemberSessionId;

    @BeforeEach
    void setUp() throws IOException {
      member = memberTestHelper.generate();
      otherMember = memberTestHelper.generate();
      memberSessionId = sessionService.createSessionId(member.getId(), ROLE_회원);
      otherMemberSessionId = sessionService.createSessionId(otherMember.getId(), ROLE_회원);
    }

    @Test
    @DisplayName("회원 프로필 조회를 성공해야 한다")
    void 회원_프로필_조회를_성공해야_한다() throws Exception {
      memberService.follow(member, memberTestHelper.builder().realName(RealName.from("일일")).build().getId());
      memberService.follow(member, memberTestHelper.builder().realName(RealName.from("일일")).build().getId());
      memberService.follow(memberTestHelper.builder().realName(RealName.from("삼삼")).build(), member.getId());
      memberService.follow(memberTestHelper.builder().realName(RealName.from("삼삼")).build(), member.getId());

      String securedValue = getSecuredValue(MemberController.class, "getMemberProfile");

      em.flush();
      em.clear();

      mockMvc.perform(get("/members/{memberId}/profile", member.getId())
              .cookie(new Cookie(SESSION_COOKIE_NAME, memberSessionId))
              .contentType(APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.follower[0].name").value("삼삼"))
          .andExpect(jsonPath("$.followee[0].name").value("일일"))
          .andDo(document("get-member-profile",
              requestCookies(
                  cookieWithName(SESSION_COOKIE_NAME)
                      .description("OPAQUE SESSION ID %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("memberId")
                      .description("조회하고자 하는 회원의 ID값")
              ),
              responseFields(
                  getMemberProfileResponse()
              )));
    }

    @Test
    @DisplayName("다른 회원의 프로필을 조회할 때는 학번이 노출되면 안된다.")
    public void 다른_회원의_프로필_조회할_때는_학번이_노출되면_안된다() throws Exception {
      memberService.follow(member, memberTestHelper.builder().realName(RealName.from("일일")).build().getId());
      memberService.follow(memberTestHelper.builder().realName(RealName.from("삼삼")).build(), member.getId());

      em.flush();
      em.clear();

      mockMvc.perform(get("/members/{memberId}/profile", member.getId())
              .cookie(new Cookie(SESSION_COOKIE_NAME, otherMemberSessionId))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.follower[0].name").value("삼삼"))
          .andExpect(jsonPath("$.followee[0].name").value("일일"))
          .andExpect(jsonPath("$.studentId").value("default"));
    }
  }

  @Nested
  @DisplayName("회원 타입 변경 테스트")
  class UpdateMemberTypeTest {

    private Member member, otherMember, admin;
    private Long typeId;
    private String memberSessionId, adminSessionId;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.generate();
      otherMember = memberTestHelper.generate();
      admin = memberTestHelper.generate();
      memberSessionId = sessionService.createSessionId(member.getId(), ROLE_회원);
      adminSessionId = sessionService.createSessionId(member.getId(), ROLE_회원, ROLE_회장);
      typeId = 3L;
    }

    @Test
    @DisplayName("유효한 요청일 때 회원 타입 변경은 성공한다.")
    public void 유효한_요청일_때_회원_타입_변경은_성공한다() throws Exception {
      String securedValue = getSecuredValue(MemberController.class, "updateMemberType");
      List<Long> memberSet = List.of(member.getId(), otherMember.getId());

      UpdateMemberTypeRequest request = UpdateMemberTypeRequest.builder()
          .memberIds(memberSet)
          .build();

      mockMvc.perform(patch("/members/types/{typeId}", typeId)
              .cookie(new Cookie(SESSION_COOKIE_NAME, adminSessionId))
              .content(asJsonString(request))
              .contentType(APPLICATION_JSON))
          .andExpect(status().isNoContent())
          .andDo(document("update-member-type",
              requestCookies(
                  cookieWithName(SESSION_COOKIE_NAME)
                      .description("OPAQUE SESSION ID %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("typeId").description("변경할 회원 타입")
              ),
              requestFields(
                  fieldWithPath("memberIds").description("하나 이상의 회원 ID")
              )));
    }

    @Test
    @DisplayName("회원 ID가 없을 때 타입 변경은 실패해야 한다.")
    public void 회원_ID가_없을_때_타입_변경은_실패해야_한다() throws Exception {
      List<Long> memberSet = List.of();

      UpdateMemberTypeRequest request = UpdateMemberTypeRequest.builder()
          .memberIds(memberSet)
          .build();

      mockMvc.perform(patch("/members/types/{typeId}", typeId)
              .cookie(new Cookie(SESSION_COOKIE_NAME, memberSessionId))
              .content(asJsonString(request))
              .contentType(APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 ID가 중복될 때 타입 변경은 실패해야 한다.")
    public void 회원_ID가_중복될_때_타입_변경은_실패해야_한다() throws Exception {
      List<Long> memberSet = List.of(member.getId(), otherMember.getId(), otherMember.getId());

      UpdateMemberTypeRequest request = UpdateMemberTypeRequest.builder()
          .memberIds(memberSet)
          .build();

      mockMvc.perform(patch("/members/types/{typeId}", typeId)
              .cookie(new Cookie(SESSION_COOKIE_NAME, memberSessionId))
              .content(asJsonString(request))
              .contentType(APPLICATION_JSON))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("회원 이메일 변경 테스트")
  class UpdateMemberProfileEmailAddressTest {

    private Member member, otherMember;
    private String memberSessionId;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.generate();
      otherMember = memberTestHelper.generate();
      memberSessionId = sessionService.createSessionId(member.getId(), ROLE_회원);
    }

    @Test
    @DisplayName("유효한 요청일 경우 이메일 인증 코드 발송에 성공해야 한다.")
    public void 유효한_요청일_경우_이메일_인증_코드_발송에_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(MemberController.class, "emailAuth");

      EmailAuthRequest request = EmailAuthRequest.from("test@test.com");

      mockMvc.perform(post("/members/email-auth")
              .cookie(new Cookie(SESSION_COOKIE_NAME, memberSessionId))
              .content(asJsonString(request))
              .contentType(APPLICATION_JSON))
          .andExpect(status().isOk())
          .andDo(document("member-email-auth",
              requestCookies(
                  cookieWithName(SESSION_COOKIE_NAME)
                      .description("OPAQUE SESSION ID %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("email").description("인증 코드를 보낼 이메일 주소")
              )));
    }

    @Test
    @DisplayName("유효한 요청일 경우 회원 이메일 변경에 성공해야 한다.")
    void 유효한_요청일_경우_회원_이메일_변경에_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(MemberController.class, "updateMemberEmail");
      UpdateMemberEmailAddressRequest request = UpdateMemberEmailAddressRequest.builder()
          .email("test@test.com")
          .auth("authTest")
          .password("truePassword")
          .build();

      doNothing().when(memberProfileService)
          .updateProfileEmailAddress(any(Member.class), anyString(), anyString(), anyString());

      mockMvc.perform(patch("/members/email")
              .cookie(new Cookie(SESSION_COOKIE_NAME, memberSessionId))
              .content(asJsonString(request))
              .contentType(APPLICATION_JSON))
          .andExpect(status().isNoContent())
          .andDo(document("update-member-emailAddress",
              requestCookies(
                  cookieWithName(SESSION_COOKIE_NAME)
                      .description("OPAQUE SESSION ID %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("email").description("회원의 변경할 이메일"),
                  fieldWithPath("auth").description("이메일 인증 코드"),
                  fieldWithPath("password").description("비밀번호 검증")
              )));
    }
  }

  @Nested
  @DisplayName("회원 삭제 테스트")
  class DeleteMemberTest {

    private Member member, admin;
    private String memberSessionId, adminSessionId;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.builder()
          .password(Password.from("testPassword"))
          .build();
      memberSessionId = sessionService.createSessionId(member.getId(), ROLE_회원);
      admin = memberTestHelper.builder().build();
      adminSessionId = sessionService.createSessionId(member.getId(), ROLE_회원, ROLE_회장);
    }

    @Test
    @DisplayName("유효한 요청일 경우 회원 탈퇴에 성공한다.")
    public void 유효한_요청일_경우_회원_탈퇴에_성공한다() throws Exception {
      String securedValue = getSecuredValue(MemberController.class, "deleteMember");
      DeleteMemberRequest request = DeleteMemberRequest.from("testPassword");

      mockMvc.perform(delete("/members")
              .cookie(new Cookie(SESSION_COOKIE_NAME, memberSessionId))
              .content(asJsonString(request))
              .contentType(APPLICATION_JSON))
          .andExpect(status().isNoContent())
          .andDo(document("delete-member",
              requestCookies(
                  cookieWithName(SESSION_COOKIE_NAME)
                      .description("OPAQUE SESSION ID %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("rawPassword").description("현재 비밀번호")
              )));
    }

    @Test
    @DisplayName("관리자 권한으로 회원 탈퇴는 성공한다.")
    public void 관리자_권한으로_회원_탈퇴는_성공한다() throws Exception {
      String securedValue = getSecuredValue(MemberController.class, "deleteMemberByAdmin");

      List<Long> memberSet = List.of(member.getId());

      AdminDeleteMemberRequest request = AdminDeleteMemberRequest.builder()
          .memberIds(memberSet)
          .build();

      mockMvc.perform(delete("/members/admin")
              .cookie(new Cookie(SESSION_COOKIE_NAME, adminSessionId))
              .content(asJsonString(request))
              .contentType(APPLICATION_JSON))
          .andExpect(status().isNoContent())
          .andDo(document("admin-delete-member",
              requestCookies(
                  cookieWithName(SESSION_COOKIE_NAME)
                      .description("OPAQUE SESSION ID %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("memberIds").description("하나 이상의 회원 ID")
              )));
    }

    @Test
    @DisplayName("비밀번호가 틀릴 시 탈퇴에 실패한다.")
    public void 비밀번호가_틀릴_시_탈퇴에_실패한다() throws Exception {
      DeleteMemberRequest request = DeleteMemberRequest.from("falsePassword");

      mockMvc.perform(delete("/members")
              .cookie(new Cookie(SESSION_COOKIE_NAME, memberSessionId))
              .content(asJsonString(request))
              .contentType(APPLICATION_JSON))
          .andExpect(status().isBadRequest());
    }
  }
}
