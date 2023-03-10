package com.keeper.homepage.domain.post.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.post.application.PostService.EXAM_ACCESSIBLE_ATTENDANCE_COUNT;
import static com.keeper.homepage.domain.post.application.PostService.EXAM_ACCESSIBLE_COMMENT_COUNT;
import static com.keeper.homepage.domain.post.application.PostService.EXAM_ACCESSIBLE_POINT;
import static com.keeper.homepage.domain.post.dto.request.PostCreateRequest.MAX_REQUEST_PASSWORD_LENGTH;
import static com.keeper.homepage.domain.post.dto.request.PostCreateRequest.MAX_REQUEST_TITLE_LENGTH;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.dto.request.PostUpdateRequest;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.category.Category;
import com.keeper.homepage.global.util.web.WebUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class PostControllerTest extends PostApiTestHelper {

  private Category category;
  private MockMultipartFile thumbnail, file;
  private final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
  private Member member, other;
  private String memberToken, otherToken;
  private Post post;

  private static final LocalDate now = LocalDate.now();

  @BeforeEach
  void setUp() throws IOException {
    member = memberTestHelper.builder().point(EXAM_ACCESSIBLE_POINT).build();
    other = memberTestHelper.generate();
    memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(), ROLE_회원);
    otherToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, other.getId(), ROLE_회원);
    category = categoryTestHelper.generate();
    thumbnail = thumbnailTestHelper.getSmallThumbnailFile();
    file = new MockMultipartFile("files", "testImage_1x1.png", "image/png",
        new FileInputStream("src/test/resources/images/testImage_1x1.png"));
    post = Post.builder()
        .title("게시글 제목")
        .content("게시글 내용")
        .member(member)
        .ipAddress(WebUtil.getUserIP())
        .build();
  }

  @Nested
  @DisplayName("게시글 생성")
  class CreatePost {

    private final long postId = 1;

    @Test
    @DisplayName("썸네일과 파일이 포함된 게시글을 생성하면 게시글 생성이 성공한다.")
    void should_201CREATED_when_createPostWithThumbnailAndFiles() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "createPost");

      mockCreatePostService();
      addAllParams();

      callCreatePostApiWithFiles(memberToken, thumbnail, file, params)
          .andExpect(status().isCreated())
          .andExpect(header().string("location", "/posts/" + postId))
          .andDo(document("create-post",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              queryParameters(
                  parameterWithName("title")
                      .description("게시글 제목을 입력해주세요. (최대 가능 길이 : " + MAX_REQUEST_TITLE_LENGTH + ")"),
                  parameterWithName("content")
                      .description("게시글 내용을 입력해주세요."),
                  parameterWithName("allowComment")
                      .description("댓글 허용 여부 (null일 때 default : " + true + ")")
                      .optional(),
                  parameterWithName("isNotice")
                      .description("공지글 여부 (null일 때 default : " + false + ")")
                      .optional(),
                  parameterWithName("isSecret")
                      .description("비밀글 여부 (null일 때 default : " + false + ")")
                      .optional(),
                  parameterWithName("isTemp")
                      .description("임시 저장글 여부 (null일 때 default : " + false + ")")
                      .optional(),
                  parameterWithName("password")
                      .description(
                          "게시글 비밀번호를 입력해주세요. (최대 가능 길이 : " + MAX_REQUEST_PASSWORD_LENGTH
                              + ", 비밀글일 경우 필수값입니다.)")
                      .optional(),
                  parameterWithName("categoryId")
                      .description("게시글 카테고리를 입력해주세요.")
              ),
              requestParts(
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
      addAllParams();

      callCreatePostApiWithFile(memberToken, thumbnail, params)
          .andExpect(status().isCreated())
          .andExpect(header().string("location", "/posts/" + postId));
    }

    @Test
    @DisplayName("파일만 포함된 게시글을 생성해도 게시글 생성이 성공한다.")
    void should_201CREATED_when_createPostWithFiles() throws Exception {
      mockCreatePostService();
      addAllParams();

      callCreatePostApiWithFile(memberToken, file, params)
          .andExpect(status().isCreated())
          .andExpect(header().string("location", "/posts/" + postId));
    }

    @Test
    @DisplayName("썸네일과 파일이 모두 없는 게시글을 생성해도 게시글 생성이 성공한다.")
    void should_201CREATED_when_createPost() throws Exception {
      mockCreatePostService();
      addAllParams();

      callCreatePostApi(memberToken, params)
          .andExpect(status().isCreated())
          .andExpect(header().string("location", "/posts/" + postId));
    }

    @Test
    @DisplayName("비밀번호가 없는 게시글을 생성해도 게시글 생성이 성공한다.")
    void should_201CREATED_when_createPostWithNoPassword() throws Exception {
      mockCreatePostService();
      params.add("title", "게시글 제목");
      params.add("content", "게시글 내용");
      params.add("allowComment", "true");
      params.add("isNotice", "false");
      params.add("isSecret", "false");
      params.add("isTemp", "false");
      params.add("categoryId", category.getId().toString());

      callCreatePostApi(memberToken, params)
          .andExpect(status().isCreated())
          .andExpect(header().string("location", "/posts/" + postId));
    }

    @Test
    @DisplayName("비밀글의 경우 비밀번호가 없으면 게시글 생성은 실패한다.")
    public void should_fail_when_secretPostWithoutPassword() throws Exception {
      params.add("title", "게시글 제목");
      params.add("content", "게시글 내용");
      params.add("isSecret", "true");
      params.add("categoryId", category.getId().toString());

      callCreatePostApi(memberToken, params)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 제목이 빈칸일 경우 게시글 생성은 실패한다.")
    void should_400BadRequest_when_blankTitle() throws Exception {
      params.add("title", " ");
      params.add("content", "게시글 내용");
      params.add("categoryId", category.getId().toString());

      callCreatePostApi(memberToken, params)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 내용이 빈칸일 경우 게시글 생성은 실패한다.")
    void should_400BadRequest_when_blankContent() throws Exception {
      params.add("title", "게시글 제목");
      params.add("content", " ");
      params.add("categoryId", category.getId().toString());

      callCreatePostApi(memberToken, params)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 제목이 최대 글자를 넘은 경우 게시글 생성은 실패한다.")
    void should_400BadRequest_when_tooLongTitle() throws Exception {
      params.add("title", "a".repeat(MAX_REQUEST_TITLE_LENGTH + 1));
      params.add("content", "게시글 내용");
      params.add("categoryId", category.getId().toString());

      callCreatePostApi(memberToken, params)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 비밀번호가 최대 글자를 넘은 경우 게시글 생성은 실패한다.")
    void should_400BadRequest_when_tooLongPassword() throws Exception {
      params.add("title", "게시글 제목");
      params.add("content", "게시글 내용");
      params.add("password", "a".repeat(MAX_REQUEST_PASSWORD_LENGTH + 1));
      params.add("categoryId", category.getId().toString());

      callCreatePostApi(memberToken, params)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 카테고리 ID가 null일 경우 게시글 생성은 실패한다.")
    void should_400BadRequest_when_WithoutCategoryId() throws Exception {
      params.add("title", "게시글 제목");
      params.add("content", "게시글 내용");

      callCreatePostApi(memberToken, params)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시글 카테고리 ID가 유효하지 않을 경우 게시글 생성은 실패한다.")
    void should_400BadRequest_when_inValidCategoryId() throws Exception {
      params.add("title", "게시글 제목");
      params.add("content", "게시글 내용");
      params.add("categoryId", String.valueOf(-1));

      callCreatePostApi(memberToken, params)
          .andExpect(status().isBadRequest());
    }

    private void mockCreatePostService() {
      doReturn(postId).when(postService)
          .create(any(Post.class), anyLong(), any(), any());
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
  }

  @Nested
  @DisplayName("게시글 조회")
  class FindPost {

    private static final long virtualPostId = 1;

    @BeforeEach
    void setUp() throws IOException {
      for (int i = 0; i < EXAM_ACCESSIBLE_COMMENT_COUNT; i++) {
        commentRepository.save(commentTestHelper.builder().member(member).build());
      }
      for (int i = 0; i < EXAM_ACCESSIBLE_ATTENDANCE_COUNT; i++) {
        attendanceRepository
            .save(attendanceTestHelper.builder().member(member).date(now.plusDays(i)).build());
      }
    }

    @Test
    @DisplayName("게시글을 조회하면 성공적으로 조회된다.")
    public void should_success_when_getPost() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "getPost");

      long postId = postService.create(post, category.getId(), thumbnail, List.of(thumbnail));
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
              responseFields(
                  fieldWithPath("categoryName").description("게시글 카테고리의 이름"),
                  fieldWithPath("title").description("게시글의 타이틀"),
                  fieldWithPath("writerName").description("게시글 작성자의 이름(익명 게시판일 경우 \"익명\")"),
                  fieldWithPath("registerTime").description("게시글 등록 시간"),
                  fieldWithPath("updateTime").description("게시글 수정 시간"),
                  fieldWithPath("visitCount").description("게시글 조회수"),
                  fieldWithPath("thumbnailPath").description("게시글 썸네일 주소"),
                  fieldWithPath("content").description("게시글 내용"),
                  fieldWithPath("files").description("게시글 첨부파일 리스트"),
                  fieldWithPath("files[].id").description("게시글 첨부파일 ID"),
                  fieldWithPath("files[].name").description("게시글 첨부파일 이름"),
                  fieldWithPath("files[].path").description("게시글 첨부파일 경로"),
                  fieldWithPath("files[].size").description("게시글 첨부파일 크기"),
                  fieldWithPath("files[].uploadTime").description("게시글 첨부파일 업로드 시간"),
                  fieldWithPath("files[].ipAddress").description("게시글 첨부파일 IP 주소"),
                  fieldWithPath("likeCount").description("게시글의 좋아요 수"),
                  fieldWithPath("dislikeCount").description("게시글의 싫어요 수"),
                  fieldWithPath("allowComment").description("댓글 허용 여부"),
                  fieldWithPath("isNotice").description("공지글 여부"),
                  fieldWithPath("isSecret").description("비밀글 여부"),
                  fieldWithPath("isTemp").description("임시 저장글 여부")
              )));
    }

    @Test
    @DisplayName("비밀 게시글의 경우 패스워드가 일치하면 조회가 성공한다.")
    public void should_success_when_samePassword() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "getPost");

      Post post = Post.builder()
          .title("게시글 제목")
          .content("게시글 내용")
          .member(other)
          .ipAddress(WebUtil.getUserIP())
          .password("비밀비밀")
          .build();

      Long postId = postService.create(post, category.getId(), thumbnail, List.of(thumbnail));
      em.flush();
      em.clear();

      callFindPostApiWithPassword(memberToken, postId, "비밀비밀")
          .andExpect(status().isOk())
          .andDo(document("find-secret-post",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("postId").description("조회하고자 하는 게시글의 ID")
              ),
              queryParameters(
                  parameterWithName("password")
                      .description("조회하고자 하는 게시글의 비밀번호. 단, 작성자가 본인인 경우 없어도 됩니다.")
              ),
              responseFields(
                  fieldWithPath("categoryName").description("게시글 카테고리의 이름"),
                  fieldWithPath("title").description("게시글의 타이틀"),
                  fieldWithPath("writerName").description("게시글 작성자의 이름(익명 게시판일 경우 \"익명\")"),
                  fieldWithPath("registerTime").description("게시글 등록 시간"),
                  fieldWithPath("updateTime").description("게시글 수정 시간"),
                  fieldWithPath("visitCount").description("게시글 조회수"),
                  fieldWithPath("thumbnailPath").description("게시글 썸네일 주소"),
                  fieldWithPath("content").description("게시글 내용"),
                  fieldWithPath("files").description("게시글 첨부파일 리스트"),
                  fieldWithPath("files[].id").description("게시글 첨부파일 ID"),
                  fieldWithPath("files[].name").description("게시글 첨부파일 이름"),
                  fieldWithPath("files[].path").description("게시글 첨부파일 경로"),
                  fieldWithPath("files[].size").description("게시글 첨부파일 크기"),
                  fieldWithPath("files[].uploadTime").description("게시글 첨부파일 업로드 시간"),
                  fieldWithPath("files[].ipAddress").description("게시글 첨부파일 IP 주소"),
                  fieldWithPath("likeCount").description("게시글의 좋아요 수"),
                  fieldWithPath("dislikeCount").description("게시글의 싫어요 수"),
                  fieldWithPath("allowComment").description("댓글 허용 여부"),
                  fieldWithPath("isNotice").description("공지글 여부"),
                  fieldWithPath("isSecret").description("비밀글 여부"),
                  fieldWithPath("isTemp").description("임시 저장글 여부")
              )));
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
      long postId = postTestHelper.builder()
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

      long postId = postService.create(post, category.getId(), thumbnail, List.of(file));
      addAllParams();

      callUpdatePostApiWithFiles(memberToken, postId, thumbnail, file, params)
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
              queryParameters(
                  parameterWithName("title")
                      .description("게시글 제목을 입력해주세요. (최대 가능 길이 : " + MAX_REQUEST_TITLE_LENGTH + ")"),
                  parameterWithName("content")
                      .description("게시글 내용을 입력해주세요."),
                  parameterWithName("allowComment")
                      .description("댓글 허용 여부 (null일 때 default : " + true + ")")
                      .optional(),
                  parameterWithName("isNotice")
                      .description("공지글 여부 (null일 때 default : " + false + ")")
                      .optional(),
                  parameterWithName("isSecret")
                      .description("비밀글 여부 (null일 때 default : " + false + ")")
                      .optional(),
                  parameterWithName("isTemp")
                      .description("임시 저장글 여부 (null일 때 default : " + false + ")")
                      .optional(),
                  parameterWithName("password")
                      .description(
                          "게시글 비밀번호를 입력해주세요. (최대 가능 길이 : " + MAX_REQUEST_PASSWORD_LENGTH
                              + ", 비밀글일 경우 필수값입니다.)")
                      .optional()
              ),
              requestParts(
                  partWithName("thumbnail").description("게시글의 썸네일")
                      .optional(),
                  partWithName("files").description("게시글의 첨부 파일")
                      .optional()
              ),
              responseHeaders(
                  headerWithName("Location").description("수정한 게시글을 불러오는 URI 입니다.")
              )));
    }

    @Test
    @DisplayName("내가 작성한 게시글이 아닐 경우 게시글 수정은 실패한다.")
    public void should_fail_when_writerIsNotMe() throws Exception {
      long postId = postService.create(post, category.getId(), thumbnail, List.of(thumbnail));
      addAllParams();

      callUpdatePostApiWithFiles(otherToken, postId, thumbnail, file, params)
          .andExpect(status().isForbidden());
    }

    private void addAllParams() {
      params.add("title", "게시글 제목");
      params.add("content", "게시글 내용");
      params.add("allowComment", "true");
      params.add("isNotice", "false");
      params.add("isSecret", "false");
      params.add("isTemp", "false");
      params.add("password", "게시글 비밀번호");
    }
  }

  @Nested
  @DisplayName("게시글 삭제")
  class DeletePost {

    @Test
    @DisplayName("내가 작성한 게시글인 경우 게시글 삭제는 성공한다.")
    public void should_success_when_writerIsMe() throws Exception {
      String securedValue = getSecuredValue(PostController.class, "deletePost");

      long postId = postService.create(post, category.getId(), thumbnail, List.of(file));

      callDeletePostApi(memberToken, postId)
          .andExpect(status().isNoContent())
          .andDo(document("delete-post",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("postId")
                      .description("삭제하고자 하는 게시글의 ID")
              )));
    }

    @Test
    @DisplayName("내가 작성한 게시글이 아닌 경우 게시글 삭제는 실패한다.")
    public void should_success_when_writerIsNotMe() throws Exception {
      long postId = postService.create(post, category.getId(), thumbnail, List.of(file));

      callDeletePostApi(otherToken, postId)
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("게시글 좋아요 싫어요")
  class LikeDislikePost {

    private long postId;

    @BeforeEach
    void setUp() throws IOException {
      Post post = postTestHelper.generate();
      postId = post.getId();
    }

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
}
