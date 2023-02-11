package com.keeper.homepage.domain.posting.api;

import static com.keeper.homepage.domain.posting.entity.Posting.MAX_PASSWORD_LENGTH;
import static com.keeper.homepage.domain.posting.entity.Posting.MAX_TITLE_LENGTH;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.posting.entity.category.Category;
import jakarta.servlet.http.Cookie;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class PostingControllerTest extends IntegrationTest {

  private Member member;
  private Cookie accessToken;
  private Category category;
  private MockMultipartFile thumbnail, file;
  private final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

  @BeforeEach
  void setUp() throws IOException {
    member = memberTestHelper.generate();
    accessToken = memberTestHelper.getTokenCookies(member)[0];
    category = categoryTestHelper.generate();
    thumbnail = thumbnailTestHelper.getSmallThumbnailFile();
    file = new MockMultipartFile("files", "testImage_1x1.png", "image/png",
        new FileInputStream("src/test/resources/images/testImage_1x1.png"));
  }

  @Nested
  @DisplayName("게시글 생성")
  class CreatePost {

    @Test
    @DisplayName("썸네일과 파일이 포함된 게시글을 생성하면 게시글 생성이 성공한다.")
    void should_201CREATED_when_createPostWithThumbnailAndFiles() throws Exception {
      addAllParams();

      callCreatePostApiWithThumbnailAndFiles()
          .andExpect(status().isCreated())
          .andDo(document("create-post",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("사용자 인증에 필요한 ACCESS TOKEN")),
              queryParameters(
                  parameterWithName("title")
                      .description("게시글 제목을 입력해주세요. (최대 가능 길이 :" + MAX_TITLE_LENGTH),
                  parameterWithName("content")
                      .description("게시글 내용을 입력해주세요."),
                  parameterWithName("allowComment")
                      .description("댓글 허용 여부 (null일 때 default :" + true)
                      .optional(),
                  parameterWithName("isNotice")
                      .description("공지글 여부 (null일 때 default :" + false)
                      .optional(),
                  parameterWithName("isSecret")
                      .description("비밀글 여부 (null일 때 default :" + false)
                      .optional(),
                  parameterWithName("isTemp")
                      .description("임시 저장글 여부 (null일 때 default :" + false)
                      .optional(),
                  parameterWithName("password")
                      .description("게시글 비밀번호를 입력해주세요. (최대 가능 길이 :" + MAX_PASSWORD_LENGTH)
                      .optional(),
                  parameterWithName("categoryId")
                      .description("게시글 카테고리를 입력해주세요.")
              ),
              requestParts(
                  partWithName("thumbnail").description("게시글의 썸네일")
                      .optional(),
                  partWithName("files").description("게시글의 첨부 파일")
                      .optional()
              )));
    }

    @Test
    @DisplayName("썸네일만 포함된 게시글을 생성해도 게시글 생성이 성공한다.")
    void should_201CREATED_when_createPostWithThumbnail() throws Exception {
      addAllParams();

      callCreatePostApiWithThumbnail()
          .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("파일만 포함된 게시글을 생성해도 게시글 생성이 성공한다.")
    void should_201CREATED_when_createPostWithFiles() throws Exception {
      addAllParams();

      callCreatePostApiWithFiles()
          .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("썸네일과 파일이 모두 없는 게시글을 생성해도 게시글 생성이 성공한다.")
    void should_201CREATED_when_createPost() throws Exception {
      addAllParams();

      callCreatePostApiWithoutFiles()
          .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("비밀번호가 없는 게시글을 생성해도 게시글 생성이 성공한다.")
    void should_201CREATED_when_createPostWithNoPassword() throws Exception {
      addParamsExceptPassword();

      callCreatePostApiWithoutFiles()
          .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("게시글 제목이 빈칸일 경우 게시글 생성은 실패한다.")
    void should_400BadRequest_when_blankTitle() throws Exception {
      params.add("title", " ");
      params.add("content", "게시글 내용");
      params.add("categoryId", category.getId().toString());

      callCreatePostApiWithoutFiles()
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 내용이 빈칸일 경우 게시글 생성은 실패한다.")
    void should_400BadRequest_when_blankContent() throws Exception {
      params.add("title", "게시글 제목");
      params.add("content", " ");
      params.add("categoryId", category.getId().toString());

      callCreatePostApiWithoutFiles()
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 제목이 최대 글자를 넘은 경우 게시글 생성은 실패한다.")
    void should_400BadRequest_when_tooLongTitle() throws Exception {
      params.add("title", "a".repeat(MAX_TITLE_LENGTH + 1));
      params.add("content", "게시글 내용");
      params.add("categoryId", category.getId().toString());

      callCreatePostApiWithoutFiles()
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 비밀번호가 최대 글자를 넘은 경우 게시글 생성은 실패한다.")
    void should_400BadRequest_when_tooLongPassword() throws Exception {
      params.add("title", "게시글 제목");
      params.add("content", "게시글 내용");
      params.add("password", "a".repeat(MAX_PASSWORD_LENGTH + 1));
      params.add("categoryId", category.getId().toString());

      callCreatePostApiWithoutFiles()
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 카테고리 ID가 없는 경우 게시글 생성은 실패한다.")
    void should_400BadRequest_when_WithoutCategoryId() throws Exception {
      params.add("title", "게시글 제목");
      params.add("content", "게시글 내용");

      callCreatePostApiWithoutFiles()
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 카테고리 ID가 유효하지 않을 경우 게시글 생성은 실패한다.")
    void should_400BadRequest_when_inValidCategoryId() throws Exception {
      params.add("title", "게시글 제목");
      params.add("content", "게시글 내용");
      params.add("categoryId", String.valueOf(-1));

      callCreatePostApiWithoutFiles()
          .andExpect(status().isBadRequest());
    }

    private void addAllParams() {
      params.add("title", "게시글 제목");
      params.add("content", "게시글 내용");
      params.add("allowComment", "true");
      params.add("isNotice", "false");
      params.add("isSecret", "false");
      params.add("isTemp", "false");
      params.add("password", "게시글 비밀번호");
      params.add("categoryId", category.getId().toString());
    }

    private void addParamsExceptPassword() {
      params.add("title", "게시글 제목");
      params.add("content", "게시글 내용");
      params.add("allowComment", "true");
      params.add("isNotice", "false");
      params.add("isSecret", "false");
      params.add("isTemp", "false");
      params.add("categoryId", category.getId().toString());
    }

    private ResultActions callCreatePostApiWithThumbnailAndFiles()
        throws Exception {
      return mockMvc.perform(multipart(HttpMethod.POST, "/postings/create")
          .file(thumbnail)
          .file(file)
          .cookie(accessToken)
          .queryParams(params)
          .contentType(MediaType.MULTIPART_FORM_DATA));
    }

    private ResultActions callCreatePostApiWithThumbnail()
        throws Exception {
      return mockMvc.perform(multipart(HttpMethod.POST, "/postings/create")
          .file(thumbnail)
          .cookie(accessToken)
          .queryParams(params)
          .contentType(MediaType.MULTIPART_FORM_DATA));
    }

    private ResultActions callCreatePostApiWithFiles()
        throws Exception {
      return mockMvc.perform(multipart(HttpMethod.POST, "/postings/create")
          .file(file)
          .cookie(accessToken)
          .queryParams(params)
          .contentType(MediaType.MULTIPART_FORM_DATA));
    }

    private ResultActions callCreatePostApiWithoutFiles()
        throws Exception {
      return mockMvc.perform(multipart(HttpMethod.POST, "/postings/create")
          .cookie(accessToken)
          .queryParams(params)
          .contentType(MediaType.MULTIPART_FORM_DATA));
    }
  }
}
