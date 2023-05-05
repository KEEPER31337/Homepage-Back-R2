package com.keeper.homepage.domain.study.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.study.dto.request.StudyCreateRequest.STUDY_INFORMATION_LENGTH;
import static com.keeper.homepage.domain.study.dto.request.StudyCreateRequest.STUDY_TITLE_LENGTH;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.domain.member.entity.Member;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class StudyControllerTest extends StudyApiTestHelper {

  private Member member;
  private MockMultipartFile thumbnail;
  private String memberToken;
  private final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

  @BeforeEach
  void setUp() throws IOException {
    member = memberTestHelper.builder().build();
    thumbnail = thumbnailTestHelper.getSmallThumbnailFile();
    memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(), ROLE_회원);
  }

  @Nested
  @DisplayName("스터디 생성")
  class CreateStudy {

    @Test
    @DisplayName("썸네일과 파일이 포함된 게시글을 생성하면 게시글 생성이 성공한다.")
    void should_201CREATED_when_createPostWithThumbnailAndFiles() throws Exception {
      String securedValue = getSecuredValue(StudyController.class, "createStudy");

      addAllParams(params);

      callCreateStudyApiWithThumbnail(memberToken, thumbnail, params)
          .andExpect(status().isCreated())
          .andDo(document("create-study",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue) + " 스터디 생성자는 스터디장이 됩니다.")
              ),
              queryParameters(
                  parameterWithName("title")
                      .description("스터디 이름을 입력해주세요. (최대 가능 길이 : " + STUDY_TITLE_LENGTH + ")"),
                  parameterWithName("information")
                      .description("스터디 설명을 입력해주세요. (최대 가능 길이 : " + STUDY_INFORMATION_LENGTH + ")"),
                  parameterWithName("year")
                      .description("스터디 년도를 입력해주세요."),
                  parameterWithName("season")
                      .description("스터디 학기를 입력해주세요. (1: 1학기 2: 여름학기 3: 2학기 4: 겨울학기)"),
                  parameterWithName("gitLink")
                      .description("스터디 깃허브 링크를 입력해주세요.").optional(),
                  parameterWithName("noteLink")
                      .description("스터디 노트 링크를 입력해주세요.").optional(),
                  parameterWithName("etcLink")
                      .description("스터디 기타 링크를 입력해주세요.").optional()
              ),
              requestParts(
                  partWithName("thumbnail").description("스터디의 썸네일")
                      .optional()
              )));
    }

    private void addAllParams(MultiValueMap<String, String> params) {
      params.add("title", "자바 스터디");
      params.add("information", "자바 스터디 입니다");
      params.add("year", "2023");
      params.add("season", "1");
      params.add("gitLink", "github.com");
      params.add("noteLink", "notion.com");
      params.add("etcLink", "etc.com");
    }
  }
}
