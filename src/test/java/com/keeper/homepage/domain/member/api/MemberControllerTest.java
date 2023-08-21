package com.keeper.homepage.domain.member.api;

import static com.keeper.homepage.domain.member.entity.embedded.RealName.REAL_NAME_INVALID;
import static com.keeper.homepage.domain.member.entity.embedded.StudentId.STUDENT_ID_INVALID;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.config.security.data.JwtType.REFRESH_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.pageHelper;
import static org.assertj.core.api.Assertions.assertThat;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.domain.member.dto.request.ChangePasswordRequest;
import com.keeper.homepage.domain.member.dto.request.ProfileUpdateRequest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.RealName;
import com.keeper.homepage.domain.member.entity.embedded.StudentId;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.snippet.Attributes.Attribute;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

class MemberControllerTest extends MemberApiTestHelper {

  @Nested
  class ChangePassword {

    private Member member;
    private Cookie[] tokenCookies;

    @BeforeEach
    void setup() {
      member = memberTestHelper.generate();
      tokenCookies = memberTestHelper.getTokenCookies(member);
    }

    @Test
    @DisplayName("유효한 비밀번호로 변경을 요청했을 때 204 NO CONTENT를 반환해야 한다.")
    void should_responseNoContent_when_validPassword() throws Exception {
      ChangePasswordRequest request = ChangePasswordRequest.from("password123!@#");

      mockMvc.perform(patch("/members/change-password")
              .cookie(tokenCookies)
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andExpect(status().isNoContent())
          .andExpect(header().string(HttpHeaders.LOCATION, "/members/me"))
          .andDo(document("change-password",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName()).description("ACCESS TOKEN"),
                  cookieWithName(REFRESH_TOKEN.getTokenName()).description("REFRESH TOKEN")),
              requestFields(
                  fieldWithPath("newPassword").description("새로운 패스워드")),
              responseHeaders(
                  headerWithName(HttpHeaders.LOCATION).description("비밀번호 변경한 리소스의 위치입니다."))));
    }
  }

  @Nested
  @DisplayName("회원 목록 조회")
  class GetMembers {

    private Member member;
    private String memberToken;

    @BeforeEach
    void setUp() throws IOException {
      member = memberTestHelper.builder().build();
      memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(), ROLE_회원);
    }

    @Test
    @DisplayName("유효한 요청일 경우 회원 목록 조회(검색)는 성공한다.")
    public void 유효한_요청일_경우_회원_목록_조회는_성공한다() throws Exception {
      assertThat(member.getGeneration()).isNotNull();
      String securedValue = getSecuredValue(MemberController.class, "getMembersByRealName");

      mockMvc.perform(get("/members/real-name")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andDo(document("get-members-by-real-name",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              queryParameters(
                  parameterWithName("searchName").description("회원 이름 검색어")
                      .attributes(new Attribute("format", "null : 전체 목록 조회"))
                      .optional()
              ),
              responseFields(
                  fieldWithPath("[].memberId").description("회원 ID"),
                  fieldWithPath("[].memberName").description("회원 실명"),
                  fieldWithPath("[].generation").description("회원 기수")
              )));
    }
  }

  @Nested
  @DisplayName("누적 포인트 랭킹 테스트")
  class PointRanking {

    private String memberToken;
    private final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    @BeforeEach
    void setUp() throws IOException {
      long memberId;
      memberTestHelper.builder().point(0).build();
      memberTestHelper.builder().point(100).build();
      memberId = memberTestHelper.builder().point(1000).build().getId();
      memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, memberId, ROLE_회원);
    }

    @Test
    @DisplayName("유효한 요청이면 누적 포인트 랭킹 조회는 성공해야 한다.")
    public void 유효한_요청이면_누적_포인트_랭킹_조회는_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(MemberController.class, "getPointRanks");

      params.add("page", "0");
      params.add("size", "3");
      callGetPointRankingApi(memberToken, params)
          .andExpect(status().isOk())
          .andDo(document("get-point-ranks",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
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

    private String memberToken;
    private long memberId;
    private long otherId;

    @BeforeEach
    void setUp() throws IOException {
      memberId = memberTestHelper.generate().getId();
      otherId = memberTestHelper.generate().getId();
      memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, memberId, ROLE_회원);
    }

    @Test
    @DisplayName("유효한 요청일 경우 회원 팔로우는 성공한다.")
    public void 유효한_요청일_경우_회원_팔로우는_성공한다() throws Exception {
      String securedValue = getSecuredValue(MemberController.class, "follow");

      mockMvc.perform(post("/members/{memberId}/follow", otherId)
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)))
          .andExpect(status().isCreated())
          .andDo(document("follow-member",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
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
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)))
          .andExpect(status().isNoContent())
          .andDo(document("unfollow-member",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
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
    private String memberToken;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.generate();
      memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(), ROLE_회원);
    }

    @Test
    @DisplayName("유효한 요청일 경우 회원의 프로필 내용을 수정 한다.")
    public void should_success_myProfile() throws Exception {
      String securedValue = getSecuredValue(MemberController.class, "updateProfile");

      ProfileUpdateRequest request = ProfileUpdateRequest.builder()
          .realName(RealName.from("바뀐이름"))
          .studentId(StudentId.from("202055589"))
          .birthday(LocalDate.parse("2021-01-30"))
          .build();

      callUpdateProfileApi(memberToken, request)
          .andExpect(status().isCreated())
          .andDo(document("update-profile",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("realName").description("실명을 입력해주세요. (" + REAL_NAME_INVALID + ")"),
                  fieldWithPath("studentId").description(
                      "학번을 입력해주세요. (" + STUDENT_ID_INVALID + ")"),
                  fieldWithPath("birthday").description("생년월일을 입력해주세요.").optional()
              )));
    }
  }

  @Nested
  @DisplayName("회원 프로필 썸네일 변경 테스트")
  class MemberThumbnailTest {

    private Member member;
    private MockMultipartFile thumbnail;
    private String memberToken;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.generate();
      thumbnail = thumbnailTestHelper.getThumbnailFile();
      memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(), ROLE_회원);
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
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken))
              .contentType(MediaType.MULTIPART_FORM_DATA))
          .andExpect(status().isNoContent())
          .andDo(document("update-member-thumbnail",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
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
    private String memberToken;

    @BeforeEach
    void setUp() throws IOException {
      member = memberTestHelper.generate();
      otherMember = memberTestHelper.generate();
      memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(), ROLE_회원);
    }

    @Test
    @DisplayName("회원 프로필 조회를 성공해야 한다")
    void 회원_프로필_조회를_성공해야_한다() throws Exception {
      memberService.follow(member,
          memberTestHelper.builder().realName(RealName.from("일일")).build().getId());
      memberService.follow(memberTestHelper.builder().realName(RealName.from("삼삼")).build(),
          member.getId());
      String securedValue = getSecuredValue(MemberController.class, "getMemberProfile");

      em.flush();
      em.clear();

      mockMvc.perform(get("/members/{memberId}/profile", member.getId())
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andDo(print())
          .andExpect(jsonPath("$.follower[0].name").value("삼삼"))
          .andExpect(jsonPath("$.followee[0].name").value("일일"))
          .andDo(document("get-member-profile",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("memberId")
                      .description("조회하고자 하는 회원의 ID값")
              ),
              responseFields(
                  getMemberProfileResponse()
              )));
    }
  }
}
