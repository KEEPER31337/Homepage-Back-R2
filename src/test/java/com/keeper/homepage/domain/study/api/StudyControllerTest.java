package com.keeper.homepage.domain.study.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.study.dto.request.StudyCreateRequest.STUDY_INFORMATION_LENGTH;
import static com.keeper.homepage.domain.study.dto.request.StudyCreateRequest.STUDY_TITLE_LENGTH;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.field;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.study.dto.request.StudyCreateRequest;
import com.keeper.homepage.domain.study.dto.request.StudyUpdateRequest;
import com.keeper.homepage.domain.study.entity.Study;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.snippet.Attributes.Attribute;

public class StudyControllerTest extends StudyApiTestHelper {

  private MockMultipartFile thumbnail;
  private Member member, other;
  private String memberToken, otherToken;
  private long studyId;

  @BeforeEach
  void setUp() throws IOException {
    member = memberTestHelper.builder().build();
    other = memberTestHelper.builder().build();
    thumbnail = thumbnailTestHelper.getSmallThumbnailFile();
    memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(), ROLE_회원);
    otherToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, other.getId(), ROLE_회원);
    studyId = studyTestHelper.builder().headMember(member).build().getId();
  }

  @Nested
  @DisplayName("스터디 생성")
  class CreateStudy {

    @Test
    @DisplayName("유효한 요청 시 스터디 생성은 성공한다.")
    public void 유효한_요청_시_스터디_생성은_성공한다() throws Exception {
      String securedValue = getSecuredValue(StudyController.class, "createStudy");

      StudyCreateRequest request = StudyCreateRequest.builder()
          .title("자바 스터디")
          .information("자바 스터디 입니다")
          .year(2023)
          .season(1)
          .gitLink("https://github.com/KEEPER31337/Homepage-Back-R2")
          .notionLink("https://www.notion.so/Java-Spring")
          .etcLink("etc.com")
          .memberIds(List.of(other.getId()))
          .build();

      MockPart mockPart = new MockPart("request", asJsonString(request).getBytes(StandardCharsets.UTF_8));
      mockPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

      callCreateStudyApiWithThumbnail(memberToken, thumbnail, mockPart)
          .andExpect(status().isCreated())
          .andDo(document("create-study",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue) + " 스터디 생성자는 스터디장이 됩니다.")
              ),
              requestPartFields(
                  "request",
                  fieldWithPath("title").description("스터디 이름을 입력해주세요. (최대 가능 길이 : " + STUDY_TITLE_LENGTH + ")"),
                  fieldWithPath("information")
                      .description("스터디 설명을 입력해주세요. (최대 가능 길이 : " + STUDY_INFORMATION_LENGTH + ")")
                      .optional(),
                  fieldWithPath("year").description("스터디 년도를 입력해주세요."),
                  fieldWithPath("season").attributes(new Attribute("format", "1: 1학기 2: 여름학기 3: 2학기 4: 겨울학기"))
                      .description("스터디 학기를 입력해주세요."),
                  fieldWithPath("gitLink").attributes(new Attribute("format", "\"https://github.com\"으로 시작"))
                      .description("스터디 깃허브 링크를 입력해주세요.").optional(),
                  fieldWithPath("notionLink")
                      .description("스터디 노션 링크를 입력해주세요.").optional(),
                  fieldWithPath("etcTitle")
                      .description("스터디 기타 자료 제목을 입력해주세요.").optional(),
                  fieldWithPath("etcLink")
                      .description("스터디 기타 링크를 입력해주세요.").optional(),
                  fieldWithPath("memberIds[]")
                      .description("스터디원 Id 리스트를 입력해주세요.")
              ),
              requestParts(
                  partWithName("request").description("스터디 정보"),
                  partWithName("thumbnail").description("스터디의 썸네일")
                      .optional()
              )));
    }

    @Test
    @DisplayName("깃허브 링크가 아닌 링크를 입력할 경우 스터디 생성은 실패한다.")
    public void 깃허브_링크가_아닌_링크를_입력할_경우_스터디_생성은_실패한다() throws Exception {
      StudyCreateRequest request = StudyCreateRequest.builder()
          .title("자바 스터디")
          .information("자바 스터디 입니다")
          .year(2023)
          .season(1)
          .gitLink("https://www.youtube.com/")
          .memberIds(List.of(other.getId()))
          .build();

      MockPart mockPart = new MockPart("request", asJsonString(request).getBytes(StandardCharsets.UTF_8));
      mockPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

      callCreateStudyApiWithThumbnail(memberToken, thumbnail, mockPart)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("스터디원 리스트가 비어있어도 스터디 생성은 성공해야 한다.")
    public void 스터디원_리스트가_비어있어도_스터디_생성은_성공해야_한다() throws Exception {
      StudyCreateRequest request = StudyCreateRequest.builder()
          .title("자바 스터디")
          .information("자바 스터디 입니다")
          .year(2023)
          .season(1)
          .gitLink("https://github.com/KEEPER31337/Homepage-Back-R2")
          .memberIds(List.of())
          .build();

      MockPart mockPart = new MockPart("request", asJsonString(request).getBytes(StandardCharsets.UTF_8));
      mockPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

      callCreateStudyApiWithThumbnail(memberToken, thumbnail, mockPart)
          .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("스터디원 리스트가 null일 경우 스터디 생성은 실패한다.")
    public void 스터디원_리스트가_null일_경우_스터디_생성은_실패한다() throws Exception {
      StudyCreateRequest request = StudyCreateRequest.builder()
          .title("자바 스터디")
          .information("자바 스터디 입니다")
          .year(2023)
          .season(1)
          .gitLink("https://github.com/KEEPER31337/Homepage-Back-R2")
          .build();

      MockPart mockPart = new MockPart("request", asJsonString(request).getBytes(StandardCharsets.UTF_8));
      mockPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

      callCreateStudyApiWithThumbnail(memberToken, thumbnail, mockPart)
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("스터디 삭제")
  class DeleteStudy {

    @Test
    @DisplayName("유효한 요청 시 스터디 삭제는 성공한다.")
    public void 유효한_요청_시_스터디_삭제는_성공한다() throws Exception {
      String securedValue = getSecuredValue(StudyController.class, "deleteStudy");

      callDeleteStudyApi(memberToken, studyId)
          .andExpect(status().isNoContent())
          .andDo(document("delete-study",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("studyId")
                      .description("삭제하고자 하는 스터디의 ID")
              )));
    }

    @Test
    @DisplayName("스터디장이 아닐 경우 스터디 삭제는 실패한다.")
    public void 스터디장이_아닐_경우_스터디_삭제는_실패한다() throws Exception {
      callDeleteStudyApi(otherToken, studyId)
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("스터디 조회")
  class GetStudy {

    private Study study;
    private Member member, headMember;

    @BeforeEach
    void setUp() {
      headMember = memberTestHelper.generate();
      study = studyTestHelper.builder().year(2023).season(1).headMember(headMember).build();
      member = memberTestHelper.generate();
    }

    @Test
    @DisplayName("스터디 조회는 성공해야 한다.")
    public void 스터디_조회는_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(StudyController.class, "getStudy");
      headMember.join(study);
      member.join(study);
      em.flush();
      em.clear();

      callGetStudyApi(memberToken, study.getId())
          .andExpect(status().isOk())
          .andDo(document("get-study",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("studyId").description("조회하고자 하는 스터디의 ID")
              ),
              responseFields(
                  fieldWithPath("information").description("스터디 정보"),
                  fieldWithPath("headMember.memberId").description("스터디장 회원 ID"),
                  fieldWithPath("headMember.generation").description("스터디장 회원 기수"),
                  fieldWithPath("headMember.realName").description("스터디장 회원 이름"),
                  fieldWithPath("members[].memberId").description("스터디원 회원 ID"),
                  fieldWithPath("members[].generation").description("스터디원 회원 기수"),
                  fieldWithPath("members[].realName").description("스터디원 회원 이름"),
                  fieldWithPath("links[]").description("스터디 링크 리스트"),
                  fieldWithPath("links[].title").description("스터디 링크 제목").optional(),
                  fieldWithPath("links[].content").description("스터디 링크").optional()
              )));
    }

    @Test
    @DisplayName("스터디 목록 조회는 성공해야 한다.")
    public void 스터디_목록_조회는_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(StudyController.class, "getStudies");

      callGetStudiesApi(memberToken, 2023, 1)
          .andExpect(status().isOk())
          .andDo(document("get-studies",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              queryParameters(
                  parameterWithName("year").description("조회하고자 하는 스터디 년도"),
                  parameterWithName("season").description("조회하고자 하는 스터디 학기")
              ),
              responseFields(
                  fieldWithPath("studies[].studyId").description("스터디 ID"),
                  fieldWithPath("studies[].thumbnailPath").description("스터디 썸네일 경로"),
                  fieldWithPath("studies[].title").description("스터디 이름"),
                  fieldWithPath("studies[].headId").description("스터디장 ID"),
                  fieldWithPath("studies[].headName").description("스터디장 이름 (실명)"),
                  fieldWithPath("studies[].memberCount").description("스터디원 수")
              )));
    }
  }

  @Nested
  @DisplayName("스터디 수정")
  class UpdateStudy {

    @Test
    @DisplayName("유효한 요청일 경우 스터디 수정은 성공한다.")
    public void 유효한_요청일_경우_스터디_수정은_성공한다() throws Exception {
      String securedValue = getSecuredValue(StudyController.class, "updateStudy");

      StudyUpdateRequest request = StudyUpdateRequest.builder()
          .title("자바 스터디")
          .information("자바 스터디 입니다.")
          .year(2023)
          .season(2)
          .gitLink("https://github.com/KEEPER31337/Homepage-Back-R2")
          .notionLink("https://www.notion.so/KEEPER-NEW-HOMEPAGE-PROJECT-c4fd631881d84e4daa6fa14404ac6173?pvs=4")
          .etcLink("https://plato.pusan.ac.kr/")
          .etcTitle("plato")
          .memberIds(List.of(other.getId()))
          .build();

      callUpdateStudyApi(memberToken, studyId, request)
          .andExpect(status().isCreated())
          .andDo(document("update-study",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("studyId").description("스터디 ID")
              ),
              requestFields(
                  field("title", "스터디 제목 (최대 가능 길이 : " + STUDY_TITLE_LENGTH + ")"),
                  field("information", "저자 (최대 가능 길이 : " + STUDY_INFORMATION_LENGTH + ")"),
                  field("year", "스터디 년도"),
                  field("season", "스터디 학기")
                      .attributes(new Attribute("format", "1: 1학기 2: 여름학기 3: 2학기 4: 겨울학기")),
                  field("gitLink", "깃허브 링크")
                      .attributes(new Attribute("format", "\"https://github.com\"으로 시작"))
                      .optional(),
                  field("notionLink", "노션 링크").optional(),
                  field("etcTitle", "기타 링크 이름").optional(),
                  field("etcLink", "기타 링크").optional(),
                  fieldWithPath("memberIds[]")
                      .description("스터디원 Id 리스트를 입력해주세요.")
              ),
              responseHeaders(
                  headerWithName("Location").description("수정한 스터디를 불러오는 URI 입니다.")
              )));
    }

    @Test
    @DisplayName("유효한 요청일 경우 스터디 썸네일 수정은 성공한다.")
    public void 유효한_요청일_경우_스터디_썸네일_수정은_성공한다() throws Exception {
      String securedValue = getSecuredValue(StudyController.class, "updateStudyThumbnail");
      MockMultipartFile newThumbnailFile = thumbnailTestHelper.getThumbnailFile();

      callUpdateStudyThumbnailApi(memberToken, studyId, newThumbnailFile)
          .andExpect(status().isNoContent())
          .andDo(document("update-study-thumbnail",
                  requestCookies(
                      cookieWithName(ACCESS_TOKEN.getTokenName())
                          .description("ACCESS TOKEN %s".formatted(securedValue))
                  ),
                  pathParameters(
                      parameterWithName("studyId").description("스터디 ID")
                  ),
                  requestParts(
                      partWithName("thumbnail").description("책의 썸네일 (null 값으로 보낼 경우 기본 썸네일로 지정됩니다.)")
                          .optional()
                  )
              )
          );
    }

    @Test
    @DisplayName("스터디장이 아닐 경우 스터디 수정은 실패한다.")
    public void 스터디장이_아닐_경우_스터디_수정은_실패한다() throws Exception {
      MockMultipartFile newThumbnailFile = thumbnailTestHelper.getThumbnailFile();
      callUpdateStudyThumbnailApi(otherToken, studyId, newThumbnailFile)
          .andExpect(status().isBadRequest());
    }
  }
}
