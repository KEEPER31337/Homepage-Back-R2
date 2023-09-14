package com.keeper.homepage.domain.post.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.post.dto.request.PostCreateRequest.POST_PASSWORD_LENGTH;
import static com.keeper.homepage.domain.post.dto.request.PostCreateRequest.POST_TITLE_LENGTH;
import static com.keeper.homepage.domain.post.entity.category.Category.CategoryType.자유게시판;
import static com.keeper.homepage.domain.post.entity.category.Category.getCategoryBy;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.error.ErrorCode.POST_COMMENT_NEED;
import static com.keeper.homepage.global.error.ErrorCode.POST_HAS_NOT_THAT_FILE;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.listHelper;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.pageHelper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.dto.request.PostCreateRequest;
import com.keeper.homepage.domain.post.dto.request.PostFileDeleteRequest;
import com.keeper.homepage.domain.post.dto.request.PostUpdateRequest;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.category.Category;
import com.keeper.homepage.global.util.web.WebUtil;
import jakarta.servlet.http.Cookie;
import java.io.FileInputStream;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class PostControllerTest extends PostApiTestHelper {

  private Category category;
  private MockMultipartFile thumbnail, file;
  private final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
  private Member member, other;
  private String memberToken, otherToken;
  private Post post;
  private static final long virtualPostId = 1;
  private long postId;
  private static final int EXAM_ACCESSIBLE_POINT = 30000;

  @BeforeEach
  void setUp() throws IOException {
    member = memberTestHelper.builder().point(EXAM_ACCESSIBLE_POINT).build();
    other = memberTestHelper.generate();
    memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(), ROLE_회원);
    otherToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, other.getId(), ROLE_회원);
    category = getCategoryBy(자유게시판);
    thumbnail = thumbnailTestHelper.getSmallThumbnailFile();
    file = new MockMultipartFile("files", "testImage_1x1.png", "image/png",
        new FileInputStream("src/test/resources/images/testImage_1x1.png"));
    postTestHelper.builder().category(category).build();
    post = postTestHelper.builder().member(member).build();
    postId = post.getId();
  }

  @Nested
  @DisplayName("게시글 생성")
  class CreatePost {

    private final long postId = 1;
    private MockPart mockPart;

    @BeforeEach
    void setUp() {
      PostCreateRequest request = PostCreateRequest.builder()
          .title("게시글 제목")
          .content("게시글 내용")
          .allowComment(true)
          .isNotice(false)
          .isTemp(false)
          .isSecret(false)
          .password("게시글 비밀번호")
          .categoryId(category.getId())
          .build();
      mockPart = new MockPart("request", asJsonString(request).getBytes(StandardCharsets.UTF_8));
      mockPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DisplayName("썸네일과 파일이 포함된 게시글을 생성하면 게시글 생성이 성공한다.")
    void should_201CREATED_when_createPostWithThumbnailAndFiles() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "createPost");

      mockCreatePostService();

      callCreatePostApiWithFiles(memberToken, thumbnail, file, mockPart)
          .andExpect(status().isCreated())
          .andExpect(header().string("location", "/posts/" + postId))
          .andDo(document("create-post",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              requestPartFields(
                  "request",
                  fieldWithPath("title").description("게시글 제목을 입력해주세요. (최대 가능 길이 : " + POST_TITLE_LENGTH + ")"),
                  fieldWithPath("content").description("게시글 내용을 입력해주세요. (임시 저장글이 아닐 경우 필수입니다.)")
                      .optional(),
                  fieldWithPath("allowComment").description("댓글 허용 여부"),
                  fieldWithPath("isNotice").description("공지글 여부"),
                  fieldWithPath("isSecret").description("비밀글 여부"),
                  fieldWithPath("isTemp").description("임시 저장글 여부"),
                  fieldWithPath("password")
                      .description("게시글 비밀번호를 입력해주세요. (최대 가능 길이 : " + POST_PASSWORD_LENGTH + ", 비밀글일 경우 필수값입니다.)")
                      .optional(),
                  fieldWithPath("categoryId").description("게시글 카테고리를 입력해주세요.")
              ),
              requestParts(
                  partWithName("request").description("게시글 정보"),
                  partWithName("thumbnail").description("게시글의 썸네일")
                      .optional(),
                  partWithName("files").description("게시글의 첨부 파일")
                      .optional()
              ),
              responseHeaders(
                  headerWithName("Location").description("생성한 게시글을 불러오는 URI 입니다.")
              )));
    }

    @Test
    @DisplayName("썸네일만 포함된 게시글을 생성해도 게시글 생성이 성공한다.")
    void should_201CREATED_when_createPostWithThumbnail() throws Exception {
      mockCreatePostService();

      callCreatePostApiWithFile(memberToken, thumbnail, mockPart)
          .andExpect(status().isCreated())
          .andExpect(header().string("location", "/posts/" + postId));
    }

    @Test
    @DisplayName("파일만 포함된 게시글을 생성해도 게시글 생성이 성공한다.")
    void should_201CREATED_when_createPostWithFiles() throws Exception {
      mockCreatePostService();

      callCreatePostApiWithFile(memberToken, file, mockPart)
          .andDo(print())
          .andExpect(status().isCreated())
          .andExpect(header().string("location", "/posts/" + postId));
    }

    @Test
    @DisplayName("썸네일과 파일이 모두 없는 게시글을 생성해도 게시글 생성이 성공한다.")
    void should_201CREATED_when_createPost() throws Exception {
      mockCreatePostService();

      callCreatePostApi(memberToken, mockPart)
          .andExpect(status().isCreated())
          .andExpect(header().string("location", "/posts/" + postId));
    }

    @Test
    @DisplayName("비밀번호가 없는 게시글을 생성해도 게시글 생성이 성공한다.")
    void should_201CREATED_when_createPostWithNoPassword() throws Exception {
      mockCreatePostService();
      PostCreateRequest request = PostCreateRequest.builder()
          .title("게시글 제목")
          .content("게시글 내용")
          .allowComment(true)
          .isNotice(false)
          .isTemp(false)
          .isSecret(false)
          .categoryId(category.getId())
          .build();
      MockPart mockPart = new MockPart("request", asJsonString(request).getBytes(StandardCharsets.UTF_8));
      mockPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

      callCreatePostApi(memberToken, mockPart)
          .andExpect(status().isCreated())
          .andExpect(header().string("location", "/posts/" + postId));
    }

    @Test
    @DisplayName("비밀글의 경우 비밀번호가 없으면 게시글 생성은 실패한다.")
    public void should_fail_when_secretPostWithoutPassword() throws Exception {
      PostCreateRequest request = PostCreateRequest.builder()
          .title("게시글 제목")
          .content("게시글 내용")
          .allowComment(true)
          .isNotice(false)
          .isTemp(false)
          .isSecret(true)
          .categoryId(category.getId())
          .build();
      MockPart mockPart = new MockPart("request", asJsonString(request).getBytes(StandardCharsets.UTF_8));
      mockPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

      callCreatePostApi(memberToken, mockPart)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 제목이 빈칸일 경우 게시글 생성은 실패한다.")
    void should_400BadRequest_when_blankTitle() throws Exception {
      PostCreateRequest request = PostCreateRequest.builder()
          .title(" ")
          .content("게시글 내용")
          .allowComment(true)
          .isNotice(false)
          .isTemp(false)
          .isSecret(false)
          .categoryId(category.getId())
          .build();
      MockPart mockPart = new MockPart("request", asJsonString(request).getBytes(StandardCharsets.UTF_8));
      mockPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

      callCreatePostApi(memberToken, mockPart)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 내용이 빈칸일 경우 게시글 생성은 실패한다.")
    void should_400BadRequest_when_blankContent() throws Exception {
      PostCreateRequest request = PostCreateRequest.builder()
          .title("게시글 제목")
          .content(" ")
          .allowComment(true)
          .isNotice(false)
          .isTemp(false)
          .isSecret(false)
          .categoryId(category.getId())
          .build();
      MockPart mockPart = new MockPart("request", asJsonString(request).getBytes(StandardCharsets.UTF_8));
      mockPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

      callCreatePostApi(memberToken, mockPart)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 제목이 최대 글자를 넘은 경우 게시글 생성은 실패한다.")
    void should_400BadRequest_when_tooLongTitle() throws Exception {
      PostCreateRequest request = PostCreateRequest.builder()
          .title("a".repeat(POST_TITLE_LENGTH + 1))
          .content("게시글 내용")
          .allowComment(true)
          .isNotice(false)
          .isTemp(false)
          .isSecret(false)
          .categoryId(category.getId())
          .build();
      MockPart mockPart = new MockPart("request", asJsonString(request).getBytes(StandardCharsets.UTF_8));
      mockPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

      callCreatePostApi(memberToken, mockPart)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 비밀번호가 최대 글자를 넘은 경우 게시글 생성은 실패한다.")
    void should_400BadRequest_when_tooLongPassword() throws Exception {
      PostCreateRequest request = PostCreateRequest.builder()
          .title("게시글 제목")
          .content("게시글 내용")
          .allowComment(true)
          .isNotice(false)
          .isTemp(false)
          .isSecret(false)
          .password("a".repeat(POST_PASSWORD_LENGTH + 1))
          .categoryId(category.getId())
          .build();
      MockPart mockPart = new MockPart("request", asJsonString(request).getBytes(StandardCharsets.UTF_8));
      mockPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

      callCreatePostApi(memberToken, mockPart)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 카테고리 ID가 null일 경우 게시글 생성은 실패한다.")
    void should_400BadRequest_when_WithoutCategoryId() throws Exception {
      PostCreateRequest request = PostCreateRequest.builder()
          .title("게시글 제목")
          .content("게시글 내용")
          .allowComment(true)
          .isNotice(false)
          .isTemp(false)
          .isSecret(false)
          .password("게시글 비밀번호")
          .build();
      MockPart mockPart = new MockPart("request", asJsonString(request).getBytes(StandardCharsets.UTF_8));
      mockPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

      callCreatePostApi(memberToken, mockPart)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 카테고리 ID가 유효하지 않을 경우 게시글 생성은 실패한다.")
    void should_400BadRequest_when_inValidCategoryId() throws Exception {
      PostCreateRequest request = PostCreateRequest.builder()
          .title("게시글 제목")
          .content("게시글 내용")
          .allowComment(true)
          .isNotice(false)
          .isTemp(false)
          .isSecret(false)
          .password("게시글 비밀번호")
          .categoryId(-1L)
          .build();
      MockPart mockPart = new MockPart("request", asJsonString(request).getBytes(StandardCharsets.UTF_8));
      mockPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

      callCreatePostApi(memberToken, mockPart)
          .andExpect(status().isBadRequest());
    }

    private void mockCreatePostService() {
      doReturn(postId).when(postService)
          .create(any(Post.class), anyLong(), any(), any());
    }
  }

  @Nested
  @DisplayName("게시글 조회")
  class FindPost {

    @Test
    @DisplayName("게시글을 조회하면 성공적으로 조회된다.")
    public void should_success_when_getPost() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "getPost");

      postId = postService.create(post, category.getId(), thumbnail, List.of(file));
      postTestHelper.builder().category(category).build();
      em.flush();
      em.clear();

      callFindPostApi(memberToken, postId)
          .andExpect(status().isOk())
          .andDo(document("find-post",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("postId").description("조회하고자 하는 게시글의 ID")
              ),
              queryParameters(
                  parameterWithName("password")
                      .description("조회하고자 하는 게시글의 비밀번호. 단, 작성자가 본인인 경우 필수가 아니며 비밀글이라면 필수값입니다.")
                      .optional()
              ),
              responseFields(
                  fieldWithPath("categoryId").description("게시글 카테고리의 ID"),
                  fieldWithPath("categoryName").description("게시글 카테고리의 이름"),
                  fieldWithPath("title").description("게시글의 타이틀"),
                  fieldWithPath("writerId").description("게시글 작성자의 ID(익명 게시판일 경우 \"1\")"),
                  fieldWithPath("writerName").description("게시글 작성자의 이름(익명 게시판일 경우 \"익명\")"),
                  fieldWithPath("writerThumbnailPath").description("게시글 작성자의 썸네일 경로(익명 게시판일 경우 null)"),
                  fieldWithPath("registerTime").description("게시글 등록 시간"),
                  fieldWithPath("updateTime").description("게시글 수정 시간"),
                  fieldWithPath("visitCount").description("게시글 조회수"),
                  fieldWithPath("thumbnailPath").description("게시글 썸네일 주소"),
                  fieldWithPath("content").description("게시글 내용"),
                  fieldWithPath("likeCount").description("게시글의 좋아요 수"),
                  fieldWithPath("dislikeCount").description("게시글의 싫어요 수"),
                  fieldWithPath("allowComment").description("댓글 허용 여부"),
                  fieldWithPath("isNotice").description("공지글 여부"),
                  fieldWithPath("isSecret").description("비밀글 여부"),
                  fieldWithPath("isTemp").description("임시 저장글 여부"),
                  fieldWithPath("isLike").description("좋아요 했는지 여부"),
                  fieldWithPath("isDislike").description("싫어요 했는지 여부"),
                  fieldWithPath("isRead").description("게시글 열람 여부 (시험 게시판에서만 응답)"),
                  fieldWithPath("previousPost.postId").description("이전 게시글 ID"),
                  fieldWithPath("previousPost.title").description("이전 게시글 제목"),
                  fieldWithPath("nextPost.postId").description("다음 게시글 ID"),
                  fieldWithPath("nextPost.title").description("다음 게시글 제목")
              )));
    }

    @Test
    @DisplayName("비밀 게시글의 경우 패스워드가 일치하면 조회가 성공한다.")
    public void should_success_when_samePassword() throws Exception {
      post = postTestHelper.builder().member(other).password("비밀비밀").build();
      postId = postService.create(post, category.getId(), thumbnail, List.of(file));
      em.flush();
      em.clear();

      callFindPostApiWithPassword(memberToken, postId, "비밀비밀")
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유효하지 않은 게시글은 조회할 수 없다.")
    public void should_fail_when_getInValidPost() throws Exception {
      callFindPostApi(memberToken, -1)
          .andExpect(status().isNotFound());

      callFindPostApi(memberToken, 0)
          .andExpect(status().isNotFound());

      callFindPostApi(memberToken, virtualPostId)
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("작성자가 내가 아니면 비밀글의 패스워드가 일치해야 한다.")
    public void should_fail_whenWrongPassword() throws Exception {
      postId = postTestHelper.builder()
          .member(member)
          .isSecret(true)
          .password("비밀비밀")
          .build()
          .getId();

      em.flush();
      em.clear();

      callFindPostApiWithPassword(otherToken, postId, null)
          .andExpect(status().isForbidden());

      callFindPostApiWithPassword(otherToken, postId, "다른 비밀번호")
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("게시글 수정")
  class UpdatePost {

    @Test
    @DisplayName("내가 작성한 게시글인 경우 게시글 수정은 성공한다.")
    public void should_success_when_writerIsMe() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "updatePost");

      PostUpdateRequest request = PostUpdateRequest.builder()
          .title("게시글 제목")
          .content("게시글 내용")
          .allowComment(true)
          .isNotice(false)
          .isSecret(false)
          .isTemp(false)
          .password("게시글 비밀번호")
          .build();

      callUpdatePostApi(memberToken, postId, request)
          .andExpect(status().isCreated())
          .andExpect(header().string("location", "/posts/" + postId))
          .andDo(document("update-post",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("postId")
                      .description("수정하고자 하는 게시글의 ID")
              ),
              requestFields(
                  fieldWithPath("title").description("게시글 제목을 입력해주세요. (최대 가능 길이 : " + POST_TITLE_LENGTH + ")"),
                  fieldWithPath("content").description("게시글 내용을 입력해주세요. (임시 저장글이 아닐 경우 필수입니다.)")
                      .optional(),
                  fieldWithPath("allowComment").description("댓글 허용 여부"),
                  fieldWithPath("isNotice").description("공지글 여부"),
                  fieldWithPath("isSecret").description("비밀글 여부"),
                  fieldWithPath("isTemp").description("임시 저장글 여부"),
                  fieldWithPath("password").description("게시글 비밀번호를 입력해주세요. (최대 가능 길이 : " + POST_PASSWORD_LENGTH
                          + ", 비밀글일 경우 필수값입니다.)")
                      .optional()
              ),
              responseHeaders(
                  headerWithName("Location").description("수정한 게시글을 불러오는 URI 입니다.")
              )));
    }

    @Test
    @DisplayName("유효한 요청일 경우 게시글 썸네일 수정은 성공해야 한다.")
    public void 유효한_요청일_경우_게시글_썸네일_수정은_성공해야_한다() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "updatePostThumbnail");
      MockMultipartFile newThumbnailFile = thumbnailTestHelper.getThumbnailFile();

      callUpdatePostThumbnail(memberToken, postId, newThumbnailFile)
          .andExpect(status().isNoContent())
          .andDo(document("update-post-thumbnail",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("postId")
                      .description("썸네일을 수정하고자 하는 게시글의 ID")
              ),
              requestParts(
                  partWithName("thumbnail").description("게시글의 썸네일")
                      .optional()
              )));
    }

    @Test
    @DisplayName("내가 작성한 게시글이 아닐 경우 게시글 수정은 실패한다.")
    public void should_fail_when_writerIsNotMe() throws Exception {
      PostUpdateRequest request = PostUpdateRequest.builder()
          .title("게시글 제목")
          .content("게시글 내용")
          .allowComment(true)
          .isNotice(false)
          .isSecret(false)
          .isTemp(false)
          .password("게시글 비밀번호")
          .build();

      callUpdatePostApi(otherToken, postId, request)
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("게시글 삭제")
  class DeletePost {

    @Test
    @DisplayName("내가 작성한 게시글인 경우 게시글 삭제는 성공한다.")
    public void should_success_when_writerIsMe() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "deletePost");

      callDeletePostApi(memberToken, postId)
          .andExpect(status().isOk())
          .andDo(document("delete-post",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("postId")
                      .description("삭제하고자 하는 게시글의 ID")
              ),
              responseFields(
                  fieldWithPath("categoryName").description("게시글의 카테고리 이름")
              )));
    }

    @Test
    @DisplayName("내가 작성한 게시글이 아닌 경우 게시글 삭제는 실패한다.")
    public void should_success_when_writerIsNotMe() throws Exception {
      callDeletePostApi(otherToken, postId)
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("게시글 좋아요 싫어요")
  class LikeDislikePost {

    @Test
    @DisplayName("게시글 좋아요는 성공한다.")
    public void 게시글_좋아요는_성공한다() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "likePost");

      callLikePostApi(memberToken, postId)
          .andExpect(status().isNoContent())
          .andDo(document("like-post",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("postId")
                      .description("좋아요 또는 좋아요 취소하고자 하는 게시글의 ID")
              )));
    }

    @Test
    @DisplayName("게시글 싫어요는 성공한다.")
    public void 게시글_싫어요는_성공한다() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "dislikePost");

      callDislikePostApi(memberToken, postId)
          .andExpect(status().isNoContent())
          .andDo(document("dislike-post",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("postId")
                      .description("싫어요 또는 싫어요 취소하고자 하는 게시글의 ID")
              )));
    }
  }

  @Nested
  @DisplayName("공지 게시글 목록 조회")
  class FindNoticePosts {

    @BeforeEach
    void setUp() {
      postTestHelper.builder()
          .isNotice(true)
          .category(category)
          .build();
      em.flush();
      em.clear();
    }

    @Test
    @DisplayName("공지글 목록 조회는 성공한다.")
    public void 공지글_목록_조회는_성공한다() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "getNoticePosts");

      callGetNoticePostsApi(memberToken, category.getId())
          .andExpect(status().isOk())
          .andDo(document("get-notice-posts",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              queryParameters(
                  parameterWithName("categoryId")
                      .description("조회하고자 하는 공지글 목록의 카테고리 ID")
              ),
              responseFields(
                  fieldWithPath("posts[].id").description("게시글 ID"),
                  fieldWithPath("posts[].title").description("게시글 제목"),
                  fieldWithPath("posts[].writerId").description("게시글 작성자 ID"),
                  fieldWithPath("posts[].writerName").description("게시글 작성자 실명"),
                  fieldWithPath("posts[].writerThumbnailPath").description("게시글 작성자 썸네일 주소").optional(),
                  fieldWithPath("posts[].visitCount").description("게시글 조회수"),
                  fieldWithPath("posts[].commentCount").description("게시글 댓글 개수"),
                  fieldWithPath("posts[].isSecret").description("게시글 비밀글 여부"),
                  fieldWithPath("posts[].thumbnailPath").description("게시글 썸네일 주소").optional(),
                  fieldWithPath("posts[].likeCount").description("게시글 좋아요 수"),
                  fieldWithPath("posts[].registerTime").description("게시글 작성 시간")
              )));
    }
  }

  @Nested
  @DisplayName("게시글 썸네일 삭제")
  class DeletePostThumbnail {

    @Test
    @DisplayName("유효한 요청일 경우 썸네일 삭제는 성공한다.")
    public void 유효한_요청일_경우_썸네일_삭제는_성공한다() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "deletePostThumbnail");

      callDeletePostThumbnailApi(memberToken, postId)
          .andExpect(status().isNoContent())
          .andDo(document("delete-post-thumbnail",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("postId")
                      .description("썸네일을 삭제하고자 하는 게시글의 ID")
              )));
    }
  }

  @Nested
  @DisplayName("게시글 파일 추가")
  class AddPostFile {

    @Test
    @DisplayName("유효한 요청일 경우 게시글 파일 추가는 성공한다.")
    public void 유효한_요청일_경우_게시글_파일_추가는_성공한다() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "addPostFiles");

      callAddPostFilesApi(memberToken, postId, file)
          .andExpect(status().isCreated())
          .andDo(document("add-post-files",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("postId")
                      .description("파일을 추가하고자 하는 게시글의 ID")
              ),
              requestParts(
                  partWithName("files").description("첨부 파일")
              )));
    }
  }

  @Nested
  @DisplayName("게시글 파일 제거")
  class DeletePostFile {

    private long fileId;

    @BeforeEach
    void setUp() {
      postService.addPostFiles(member, postId, List.of(thumbnail));
      fileId = postHasFileRepository.findByPost(post)
          .orElseThrow()
          .getFile()
          .getId();
    }

    @Test
    @DisplayName("유효한 요청인 경우 게시글 파일 제거는 성공한다.")
    public void 유효한_요청인_경우_게시글_파일_제거는_성공한다() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "deletePostFile");

      PostFileDeleteRequest request = PostFileDeleteRequest.builder()
          .fileIds(List.of(fileId))
          .build();

      callDeletePostFileApi(memberToken, postId, request)
          .andExpect(status().isNoContent())
          .andDo(document("delete-post-file",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("postId")
                      .description("파일을 삭제하고자 하는 게시글의 ID")
              ),
              requestFields(
                  fieldWithPath("fileIds[]").description("삭제할 파일의 Id 리스트")
              )));
    }
  }

  @Nested
  @DisplayName("게시글 목록 조회")
  class GetPosts {

    private Post post;

    @BeforeEach
    void setUp() {
      post = postTestHelper.builder().category(category).build();
      post = postTestHelper.builder().category(category).build();
      post = postTestHelper.builder().category(category).build();
    }

    @Test
    @DisplayName("유효한 요청이면 게시글 목록 조회(검색)은 성공한다.")
    public void 유효한_요청이면_게시글_목록_조회검색은_성공한다() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "getPosts");

      params.add("categoryId", String.valueOf(category.getId()));
      params.add("searchType", null);
      params.add("search", null);
      params.add("page", "0");
      params.add("size", "3");
      callGetPostsApi(memberToken, params)
          .andExpect(status().isOk())
          .andDo(document("get-posts",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              queryParameters(
                  parameterWithName("categoryId").description("게시글 카테고리 ID"),
                  parameterWithName("searchType")
                      .attributes(new Attribute("format",
                          "title: 제목, content: 내용, writer: 작성자, title+content: 제목+내용, null : 전체(검색x)"))
                      .description("검색 타입")
                      .optional(),
                  parameterWithName("search").description("검색할 단어")
                      .optional(),
                  parameterWithName("page").description("페이지 (default: 0)")
                      .optional(),
                  parameterWithName("size").description("한 페이지당 불러올 개수 (default: 10)")
                      .optional()
              ),
              responseFields(
                  pageHelper(getPostsResponse())
              )));
    }

    @Test
    @DisplayName("page 또는 size가 음수면 게시글 목록 조회는 실패한다.")
    public void page_또는_size가_음수면_게시글_목록_조회는_실패한다() throws Exception {
      params.add("categoryId", String.valueOf(category.getId()));
      params.add("searchType", null);
      params.add("search", null);
      params.add("page", "-1");
      params.add("size", "3");
      callGetPostsApi(memberToken, params)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유효한 요청이면 최근 게시글 목록 조회는 성공한다.")
    public void 유효한_요청이면_최근_게시글_목록_조회는_성공한다() throws Exception {
      em.flush();
      em.clear();
      mockMvc.perform(get("/posts/recent"))
          .andExpect(status().isOk())
          .andDo(document("get-recent-posts",
              responseFields(
                  listHelper("", getMainPostsResponse())
              )));
    }

    @Test
    @DisplayName("유효한 요청이면 트렌드 게시글 목록 조회는 성공한다.")
    public void 유효한_요청이면_트렌드_게시글_목록_조회는_성공한다() throws Exception {
      em.flush();
      em.clear();
      mockMvc.perform(get("/posts/trend"))
          .andExpect(status().isOk())
          .andDo(document("get-trend-posts",
              responseFields(
                  listHelper("", getMainPostsResponse())
              )));
    }

    @Test
    @DisplayName("유효한 요청이면 회원의 게시글 목록 조회는 성공한다.")
    public void 유효한_요청이면_회원의_게시글_목록_조회는_성공한다() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "getMemberPosts");

      em.flush();
      em.clear();
      mockMvc.perform(get("/posts/members/{memberId}", member.getId())
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)))
          .andExpect(status().isOk())
          .andDo(document("get-member-posts",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("memberId").description("회원의 ID")
              ),
              queryParameters(
                  parameterWithName("page").description("페이지 (default: 0)")
                      .optional(),
                  parameterWithName("size").description("한 페이지당 불러올 개수 (default: 10)")
                      .optional()
              ),
              responseFields(
                  pageHelper(getMemberPostsResponse())
              )));
    }

    @Test
    @DisplayName("유효한 요청이면 회원의 임시글 목록 조회는 성공한다.")
    public void 유효한_요청이면_회원의_임시글_목록_조회는_성공한다() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "getTempPosts");

      postTestHelper.builder().member(member).isTemp(true).build();
      postTestHelper.builder().member(member).isTemp(true).build();
      postTestHelper.builder().member(member).isTemp(true).build();

      em.flush();
      em.clear();
      mockMvc.perform(get("/posts/temp")
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)))
          .andExpect(status().isOk())
          .andDo(document("get-temp-posts",
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
                  pageHelper(getTempPostsResponse())
              )));
    }

  }

  @Nested
  @DisplayName("게시글의 파일 목록 조회")
  class GetPostFiles {

    @Test
    @DisplayName("유효한 요청일 경우 게시글 파일 목록 조회는 성공한다.")
    public void 유효한_요청일_경우_게시글_파일_목록_조회는_성공한다() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "getPostFiles");

      postId = postService.create(post, category.getId(), thumbnail, List.of(file));
      em.flush();
      em.clear();

      mockMvc.perform(get("/posts/{postId}/files", postId)
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)))
          .andExpect(status().isOk())
          .andDo(document("get-post-files",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("postId").description("조회하고자 하는 게시글의 ID")
              ),
              responseFields(
                  fieldWithPath("[].fileId").description("파일 ID"),
                  fieldWithPath("[].name").description("파일 이름"),
                  fieldWithPath("[].path").description("파일 경로"),
                  fieldWithPath("[].size").description("파일 크기"),
                  fieldWithPath("[].ipAddress").description("ipAddress"),
                  fieldWithPath("[].uploadTime").description("파일 업로드 시간")
              )));
    }
  }

  @Nested
  @DisplayName("게시글 파일 다운로드 테스트")
  class DownloadFile {

    @Test
    @DisplayName("유효한 요청일 경우 게시글 파일 다운로드는 성공한다.")
    public void 유효한_요청일_경우_게시글_파일_다운로드는_성공한다() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "downloadFile");

      postService.create(post, 자유게시판.getId(), thumbnail, List.of(file));
      commentTestHelper.builder().post(post).member(other).build();

      em.flush();
      em.clear();
      FileEntity file = postHasFileRepository.findByPost(post).get().getFile();

      mockMvc.perform(get("/posts/{postId}/files/{fileId}", postId, file.getId())
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), otherToken)))
          .andExpect(status().isOk())
          .andExpect(header().string(CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\""))
          .andDo(document("download-post-file",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("postId").description("게시글 ID"),
                  parameterWithName("fileId").description("파일 ID")
              ),
              responseHeaders(
                  headerWithName(CONTENT_DISPOSITION).description("파일 이름을 포함한 응답 헤더입니다.")
              )));
    }

    @Test
    @DisplayName("게시글에 댓글을 달지 않았을 경우 파일 다운로드는 실패한다.")
    public void 게시글에_댓글을_달지_않았을_경우_파일_다운로드는_실패한다() throws Exception {
      postService.create(post, 자유게시판.getId(), thumbnail, List.of(file));

      em.flush();
      em.clear();
      FileEntity file = postHasFileRepository.findByPost(post).get().getFile();

      MvcResult mvcResult = mockMvc.perform(get("/posts/{postId}/files/{fileId}", postId, file.getId())
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), otherToken)))
          .andExpect(status().isBadRequest())
          .andReturn();

      String content = mvcResult.getResponse().getContentAsString();
      assertThat(content).contains(POST_COMMENT_NEED.getMessage());
    }

    @Test
    @DisplayName("내가 작성한 게시글일 경우 댓글을 달지 않아도 파일 다운로드는 성공한다.")
    public void 내가_작성한_게시글일_경우_댓글을_달지_않아도_파일_다운로드는_성공한다() throws Exception {
      postService.create(post, 자유게시판.getId(), thumbnail, List.of(file));

      em.flush();
      em.clear();
      FileEntity file = postHasFileRepository.findByPost(post).get().getFile();

      mockMvc.perform(get("/posts/{postId}/files/{fileId}", postId, file.getId())
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("해당 게시글의 파일이 아닐 경우 파일 다운로드는 실패한다.")
    public void 해당_게시글의_파일이_아닐_경우_파일_다운로드는_실패한다() throws Exception {
      postService.create(post, 자유게시판.getId(), thumbnail, List.of(file));
      Post otherPost = Post.builder()
          .title("title")
          .content("content")
          .member(member)
          .ipAddress(WebUtil.getUserIP())
          .allowComment(false)
          .isNotice(false)
          .isSecret(false)
          .isTemp(false)
          .password("password")
          .build();
      postService.create(otherPost, 자유게시판.getId(), thumbnail, List.of(file));
      commentTestHelper.builder().post(post).member(other).build();

      em.flush();
      em.clear();
      FileEntity file = postHasFileRepository.findByPost(otherPost).get().getFile();

      MvcResult mvcResult = mockMvc.perform(get("/posts/{postId}/files/{fileId}", postId, file.getId())
              .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), otherToken)))
          .andExpect(status().isBadRequest())
          .andReturn();

      String content = mvcResult.getResponse().getContentAsString();
      assertThat(content).contains(POST_HAS_NOT_THAT_FILE.getMessage());
    }
  }
}
