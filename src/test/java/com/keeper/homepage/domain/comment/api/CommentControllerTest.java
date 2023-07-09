package com.keeper.homepage.domain.comment.api;

import static com.keeper.homepage.domain.comment.dto.request.CommentCreateRequest.MAX_REQUEST_COMMENT_LENGTH;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.domain.comment.dto.request.CommentCreateRequest;
import com.keeper.homepage.domain.comment.entity.Comment;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.entity.Post;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CommentControllerTest extends CommentApiTestHelper {

  private Member member;
  private String memberToken;
  private Post post;
  private long postId, commentId;
  private Comment comment;

  @BeforeEach
  void setUp() throws IOException {
    member = memberTestHelper.generate();
    post = postTestHelper.generate();
    postId = post.getId();
    comment = commentTestHelper.builder().post(post).member(member).build();
    commentId = comment.getId();
    memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(), ROLE_회원);
  }

  @Nested
  @DisplayName("댓글 생성")
  class CreateComment {

    private Comment parent;

    @BeforeEach
    void setUp() {
      parent = commentTestHelper.generate();
      em.flush();
      em.clear();
    }

    @Test
    @DisplayName("댓글 생성은 성공한다.")
    public void 댓글_생성은_성공한다() throws Exception {
      String securedValue = getSecuredValue(CommentController.class, "createComment");

      CommentCreateRequest request = CommentCreateRequest.builder()
          .postId(postId)
          .parentId(parent.getId())
          .content("테스트 댓글 내용")
          .build();

      callCreateCommentApi(memberToken, request)
          .andExpect(status().isCreated())
          .andExpect(header().string("location", "/comments/posts/" + postId))
          .andDo(document("create-comment",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("postId").description("댓글을 달 게시글의 ID"),
                  fieldWithPath("parentId").description("부모 댓글의 ID (대댓글이 아닌 경우 null)").optional(),
                  fieldWithPath("content").description("댓글 내용")
              ),
              responseHeaders(
                  headerWithName("Location").description("댓글 목록을 불러오는 URI 입니다.")
              )));
    }

    @Test
    @DisplayName("게시글 ID가 없는 요청은 실패한다.")
    public void 게시글_ID가_없는_요청은_실패한다() throws Exception {
      CommentCreateRequest request = CommentCreateRequest.builder()
          .postId(null)
          .parentId(parent.getId())
          .content("테스트 댓글 내용")
          .build();

      callCreateCommentApi(memberToken, request)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 내용이 공백인 경우는 실패한다.")
    public void 댓글_내용이_공백인_경우는_실패한다() throws Exception {
      CommentCreateRequest request = CommentCreateRequest.builder()
          .postId(null)
          .parentId(parent.getId())
          .content(" ")
          .build();

      callCreateCommentApi(memberToken, request)
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 최대 글자수를 넘어가면 댓글 생성은 실패한다.")
    public void 댓글_최대_글자수를_넘어가면_댓글_생성은_실패한다() throws Exception {
      CommentCreateRequest request = CommentCreateRequest.builder()
          .postId(null)
          .parentId(parent.getId())
          .content("a".repeat(MAX_REQUEST_COMMENT_LENGTH))
          .build();

      callCreateCommentApi(memberToken, request)
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("댓글 목록 조회")
  class GetComments {

    @Test
    @DisplayName("댓글 목록 조회는 성공한다.")
    public void 댓글_목록_조회는_성공한다() throws Exception {
      em.flush();
      em.clear();
      String securedValue = getSecuredValue(CommentController.class, "getComments");

      callGetCommentsApi(memberToken, postId)
          .andExpect(status().isOk())
          .andDo(document("get-comments",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("postId").description("조회하고자 하는 댓글목록의 게시글 ID")
              ),
              responseFields(
                  fieldWithPath("comments[].commentId").description("댓글 ID"),
                  fieldWithPath("comments[].writerName").description("댓글 작성자 이름"),
                  fieldWithPath("comments[].writerThumbnailPath").description("댓글 작성자의 썸네일 경로"),
                  fieldWithPath("comments[].content").description("댓글 내용"),
                  fieldWithPath("comments[].registerTime").description("댓글 등록 시간"),
                  fieldWithPath("comments[].parentId").description("부모 댓글 ID"),
                  fieldWithPath("comments[].likeCount").description("댓글 좋아요 개수"),
                  fieldWithPath("comments[].dislikeCount").description("댓글 싫어요 개수")
              )));
    }
  }

  @Nested
  @DisplayName("댓글 수정")
  class UpdateComment {

    @Test
    @DisplayName("댓글 수정은 성공한다.")
    public void 댓글_수정은_성공한다() throws Exception {
      String securedValue = getSecuredValue(CommentController.class, "updateComment");
      String content = "댓글 수정 내용";

      callUpdateCommentApi(memberToken, commentId, content)
          .andExpect(status().isCreated())
          .andExpect(header().string("location", "/comments/posts/" + postId))
          .andDo(document("update-comment",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("commentId")
                      .description("수정하고자 하는 댓글의 ID")
              ),
              queryParameters(
                  parameterWithName("content")
                      .description("수정하고자 하는 댓글의 내용")),
              responseHeaders(
                  headerWithName("Location").description("댓글 목록을 불러오는 URI 입니다.")
              )));
    }
  }

  @Nested
  @DisplayName("댓글 삭제")
  class DeleteComment {

    @Test
    @DisplayName("댓글 삭제는 성공한다.")
    public void 댓글_삭제는_성공한다() throws Exception {
      String securedValue = getSecuredValue(CommentController.class, "deleteComment");

      callDeleteCommentApi(memberToken, commentId)
          .andExpect(status().isNoContent())
          .andDo(document("delete-comment",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("commentId")
                      .description("삭제하고자 하는 댓글의 ID")
              )));
    }
  }

  @Nested
  @DisplayName("댓글 좋아요 싫어요")
  class LikeAndDislikeComment {

    @Test
    @DisplayName("댓글 좋아요 또는 좋아요 취소는 성공한다.")
    public void 댓글_좋아요_또는_좋아요_취소는_성공한다() throws Exception {
      String securedValue = getSecuredValue(CommentController.class, "likeComment");

      callLikeCommentApi(memberToken, commentId)
          .andExpect(status().isNoContent())
          .andDo(document("like-comment",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("commentId")
                      .description("좋아요 또는 좋아요 취소하고자 하는 댓글의 ID")
              )));
    }

    @Test
    @DisplayName("댓글 싫어요 또는 싫어요 취소는 성공한다.")
    public void 댓글_싫어요_또는_싫어요_취소는_성공한다() throws Exception {
      String securedValue = getSecuredValue(CommentController.class, "dislikeComment");

      callDislikeCommentApi(memberToken, commentId)
          .andExpect(status().isNoContent())
          .andDo(document("dislike-comment",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("commentId")
                      .description("싫어요 또는 싫어요 취소하고자 하는 댓글의 ID")
              )));
    }
  }
}
