package com.keeper.homepage.domain.merit.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_부회장;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_서기;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.pageHelper;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.merit.dto.request.AddMeritTypeRequest;
import com.keeper.homepage.domain.merit.dto.request.GiveMeritPointRequest;
import com.keeper.homepage.domain.merit.dto.request.SearchMeritLogListRequest;
import com.keeper.homepage.domain.merit.dto.request.UpdateMeritTypeRequest;
import com.keeper.homepage.domain.merit.entity.MeritType;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;


public class MeritControllerTest extends MeritApiTestHelper {


  private MeritType meritType, demeritType;
  private Member member, admin, otherMember;
  private String userAccessToken, adminAccessToken;

  @BeforeEach
  void setUp() throws IOException {
    meritType = meritTypeHelper.generate();
    member = memberTestHelper.generate();
    otherMember = memberTestHelper.generate();
    admin = memberTestHelper.generate();
    userAccessToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(),
        ROLE_회원);
    adminAccessToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(),
        ROLE_회장, ROLE_부회장, ROLE_서기, ROLE_회원);
  }

  @Nested
  @DisplayName("상벌점 타입 테스트")
  class MeritTypeTest {

    @Test
    @DisplayName("상벌점 타입 조회를 성공해야 한다.")
    void 상벌점_조회는_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(MeritController.class, "searchMeritType");
      mockMvc.perform(get("/merits/types")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminAccessToken)))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
          .andDo(document("search-meritType",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              responseFields(
                  pageHelper(getMeritTypeResponse())
              )));
    }

    @Test
    @DisplayName("일반 회원은 조회할 수 없다.")
    void 일반_회원은_조회할_수_없다() throws Exception {
      mockMvc.perform(get("/merits/types")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), userAccessToken)))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("상벌점 타입 생성을 성공해야 한다.")
    void 상벌점_타입_생성을_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(MeritController.class, "registerMeritType");
      AddMeritTypeRequest request = AddMeritTypeRequest.builder()
          .score(3)
          .reason("우수기술문서 작성")
          .build();

      mockMvc.perform(post("/merits/types")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminAccessToken))
              .content(asJsonString(request))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isCreated())
          .andDo(document("create-meritType",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("score").description("상벌점 점수를 입력해주세요."),
                  fieldWithPath("reason").description("상벌점 사유를 입력해주세요.")
              )));
    }

    @Test
    @DisplayName("일반 회원은 상벌점 타입을 생성할 수 없다.")
    void 일반_회원은_상벌점_타입을_생성할_수_없다() throws Exception {
      AddMeritTypeRequest request = AddMeritTypeRequest.builder()
          .score(3)
          .reason("우수기술문서 작성")
          .build();

      mockMvc.perform(post("/merits/types")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), userAccessToken))
              .content(asJsonString(request))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("상벌점 타입 수정을 성공해야 한다.")
    void 상벌점_부여_로그_수정을_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(MeritController.class, "updateMeritType");
      UpdateMeritTypeRequest request = UpdateMeritTypeRequest.builder()
          .score(5)
          .reason("거짓 스터디")
          .build();

      mockMvc.perform(put("/merits/types/{meritTypeId}", meritType.getId())
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminAccessToken))
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andExpect(status().isCreated())
          .andDo(document("update-meritType",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("score").description("수정할 점수"),
                  fieldWithPath("reason").description("수정할 사유")
              )));
    }

    @Test
    @DisplayName("일반회원은 상벌점 타입 수정을 성공할 수 없다.")
    void 일반회원은_상벌점_타입_수정을_성공할_수_없다() throws Exception {
      UpdateMeritTypeRequest request = UpdateMeritTypeRequest.builder()
          .score(5)
          .reason("거짓 스터디")
          .build();

      mockMvc.perform(put("/merits/types/{meritTypeId}", meritType.getId())
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), userAccessToken))
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.message").exists());
    }
  }

  @Nested
  @DisplayName("상벌점 로그 테스트")
  class MeritLogTest {

    @BeforeEach
    void setUp() throws IOException {
      meritLogTestHelper.generate();
      meritLogTestHelper.generate();
    }

    @Test
    @DisplayName("회원별 상벌점 목록 조회를 성공해야 한다.")
    void 회원별_상벌점_목록_조회를_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(MeritController.class, "findMeritLogByMemberId");
      meritLogTestHelper.builder().memberId(member.getId()).build();

      mockMvc.perform(
              get("/merits/members/{memberId}", member.getId())
                  .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminAccessToken)))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
          .andDo(document("search-member-meritLog",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("memberId").description("조회하고자 하는 멤버의 ID 값")
              ),
              responseFields(
                  pageHelper(getAwarderMeritLogResponse())
              )));
    }

    @Test
    @DisplayName("일반회원은 회원별 상벌점 목록 조회를 할 수 없다.")
    void 일반회원은_회원별_상벌점_목록_조회를_할_수_없다() throws Exception {
      meritLogTestHelper.builder().memberId(member.getId()).build();

      mockMvc.perform(
              get("/merits/members/{memberId}", member.getId())
                  .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), userAccessToken)))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.message").exists());


    }

    @Test
    @DisplayName("상벌점 목록 조회를 성공해야 한다.")
    void 상벌점_목록_조회를_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(MeritController.class, "searchMeritLogList");
      SearchMeritLogListRequest request = SearchMeritLogListRequest.from("ALL");

      mockMvc.perform(get("/merits")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminAccessToken))
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
          .andDo(document("search-meritLog",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              responseFields(
                  pageHelper(getMeritLogResponse())
              )));
    }

    @Test
    @DisplayName("일반회원은 상벌점 목록 조회를 할 수 없다.")
    void 일반회원은_상벌점_목록_조회를_할_수_없다() throws Exception {
      SearchMeritLogListRequest request = SearchMeritLogListRequest.from("ALL");

      mockMvc.perform(get("/merits")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), userAccessToken))
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.message").exists());
    }


    @Test
    @DisplayName("상벌점 부여 로그 생성을 성공해야 한다.")
    void 상벌점_부여_로그_생성을_성공해야_한다() throws Exception {

      String securedValue = getSecuredValue(MeritController.class, "registerMerit");
      GiveMeritPointRequest request = GiveMeritPointRequest.builder()
          .awarderId(member.getId())
          .meritTypeId(meritType.getId())
          .build();

      mockMvc.perform(multipart("/merits")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminAccessToken))
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andExpect(status().isCreated())
          .andDo(document("create-meritLog",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("awarderId").description("수여자의 ID"),
                  fieldWithPath("meritTypeId").description("상벌점 타입의 ID")
              )));
    }

    @Test
    @DisplayName("일반회원은 상벌점 부여 로그를 생성할 수 없다.")
    void 일반회원은_상벌점_부여_로그를_생성할_수_없다() throws Exception {
      GiveMeritPointRequest request = GiveMeritPointRequest.builder()
          .awarderId(member.getId())
          .meritTypeId(meritType.getId())
          .build();

      mockMvc.perform(multipart("/merits")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), userAccessToken))
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(request)))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.message").exists());
    }
  }

  @Nested
  @DisplayName("회원 통계 상벌점 목록 조회 테스트")
  class GetAllTotalMeritLogsTest {

    @BeforeEach
    void setUp() {
      meritType = meritTypeHelper.builder().merit(5).build();
      demeritType = meritTypeHelper.builder().merit(-3).build();

      meritLogTestHelper.builder()
          .memberId(member.getId())
          .memberRealName(member.getRealName())
          .memberGeneration(member.getGeneration())
          .meritType(meritType)
          .build();

      meritLogTestHelper.builder()
          .memberId(member.getId())
          .memberRealName(member.getRealName())
          .memberGeneration(member.getGeneration())
          .meritType(demeritType)
          .build();

      meritLogTestHelper.builder()
          .memberId(otherMember.getId())
          .memberRealName(otherMember.getRealName())
          .memberGeneration(otherMember.getGeneration())
          .meritType(meritType)
          .build();

      meritLogTestHelper.builder()
          .memberId(otherMember.getId())
          .memberRealName(otherMember.getRealName())
          .memberGeneration(otherMember.getGeneration())
          .meritType(meritType)
          .build();

      meritLogTestHelper.builder()
          .memberId(otherMember.getId())
          .memberRealName(otherMember.getRealName())
          .memberGeneration(otherMember.getGeneration())
          .meritType(demeritType)
          .build();
    }

    @Test
    @DisplayName("회원 통계 상벌점 목록 조회를 성공해야 한다.")
    public void 회원_통계_상벌점_목록_조회를_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(MeritController.class, "getAllTotalMeritLogs");

      mockMvc.perform(get("/merits/members")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), adminAccessToken))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andDo(document("find-total-merit-logs",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              responseFields(
                  pageHelper(getAllTotalMeritLogsResponse())
              )));
    }

  }
}
