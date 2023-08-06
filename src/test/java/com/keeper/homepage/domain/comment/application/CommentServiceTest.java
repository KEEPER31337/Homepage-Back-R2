package com.keeper.homepage.domain.comment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.comment.dto.request.CommentCreateRequest;
import com.keeper.homepage.domain.comment.entity.Comment;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.global.error.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CommentServiceTest extends IntegrationTest {

  private Member member;
  private Post post;
  private static final String DELETED_COMMENT_CONTENT = "(삭제된 댓글입니다)";

  @BeforeEach
  void setUp() {
    member = memberTestHelper.generate();
    post = postTestHelper.generate();
  }

  @Nested
  @DisplayName("댓글 생성")
  class CreateComment {

    private Comment parent;
    private CommentCreateRequest request;

    @BeforeEach
    void setUp() {
      parent = commentTestHelper.generate();
      em.flush();
      em.clear();
    }

    @Test
    @DisplayName("대댓글은 성공적으로 생성된다.")
    public void 대댓글은_성공적으로_생성된다() throws Exception {
      request = CommentCreateRequest.builder()
          .postId(post.getId())
          .parentId(parent.getId())
          .content("테스트 댓글 내용")
          .build();

      long commentId = commentService.create(member, request);

      Comment comment = commentRepository.findById(commentId).orElseThrow();

      assertThat(comment.getMember()).isEqualTo(member);
      assertThat(comment.getPost()).isEqualTo(post);
      assertThat(comment.getParent()).isEqualTo(parent);
      assertThat(comment.getContent()).isEqualTo(request.getContent());
    }

    @Test
    @DisplayName("부모 댓글의 parent는 null로 저장된다")
    public void 부모_댓글의_parent는_null로_저장된다() throws Exception {
      request = CommentCreateRequest.builder()
          .postId(post.getId())
          .parentId(null)
          .content("테스트 댓글 내용")
          .build();

      long commentId = commentService.create(member, request);

      Comment comment = commentRepository.findById(commentId).orElseThrow();

      assertThat(comment.getMember()).isEqualTo(member);
      assertThat(comment.getPost()).isEqualTo(post);
      assertThat(comment.getParent()).isEqualTo(null);
      assertThat(comment.getContent()).isEqualTo(request.getContent());
    }

    @Test
    @DisplayName("대댓글에 댓글을 달면 댓글 생성은 실패한다.")
    public void 대댓글에_댓글을_달면_댓글_생성은_실패한다() throws Exception {
      Comment comment = commentTestHelper.builder().parent(parent).build();
      long postId = post.getId();
      long commentId = comment.getId();
      em.flush();
      em.clear();

      request = CommentCreateRequest.builder()
          .postId(postId)
          .parentId(commentId)
          .content("테스트 댓글 내용")
          .build();

      assertThrows(BusinessException.class, () -> {
        commentService.create(member, request);
      });
    }
  }

  @Nested
  @DisplayName("댓글 수정")
  class UpdateComment {

    private Member other;
    private Comment comment;

    @BeforeEach
    void setUp() {
      other = memberTestHelper.generate();
      comment = commentTestHelper.builder().member(member).build();
    }

    @Test
    @DisplayName("내가 작성한 댓글이 아닌 경우 댓글 수정은 실패한다.")
    public void 내가_작성한_댓글이_아닌_경우_댓글_수정은_실패한다() throws Exception {
      assertThrows(BusinessException.class, () -> {
        commentService.update(other, comment.getId(), "수정할 댓글 내용");
      });
    }
  }

  @Nested
  @DisplayName("댓글 삭제")
  class DeleteComment {

    private Comment comment;

    @BeforeEach
    void setUp() {
      comment = commentTestHelper.builder().member(member).build();
      member.like(comment);
      member.dislike(comment);
    }

    @Test
    @DisplayName("모든 좋아요와 싫어요는 삭제된다.")
    public void 모든_좋아요와_싫어요는_삭제된다() throws Exception {
      long commentId = comment.getId();
      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).orElseThrow();
      commentService.delete(member, commentId);

      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).orElseThrow();
      assertThat(member.getCommentLikes()).isEmpty();
      assertThat(member.getCommentDislikes()).isEmpty();
    }

    @Test
    @DisplayName("댓글 작성자와 댓글의 내용이 변경된다.")
    public void 댓글_작성자와_댓글의_내용이_변경된다() throws Exception {
      commentService.delete(member, comment.getId());

      Member virtualMember = memberFindService.getVirtualMember();
      assertThat(comment.getMember().getNickname()).isEqualTo(virtualMember.getNickname());
      assertThat(comment.getContent()).isEqualTo(DELETED_COMMENT_CONTENT);
    }
  }
}
