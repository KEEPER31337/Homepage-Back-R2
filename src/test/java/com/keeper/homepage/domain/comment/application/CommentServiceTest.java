package com.keeper.homepage.domain.comment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.keeper.homepage.IntegrationTest;
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

    @BeforeEach
    void setUp() {
      parent = commentTestHelper.generate();
      em.flush();
      em.clear();
    }

    @Test
    @DisplayName("대댓글은 성공적으로 생성된다.")
    public void 대댓글은_성공적으로_생성된다() throws Exception {
      long commentId = commentService.create(member, post.getId(), parent.getId(), "테스트 댓글 내용");

      Comment comment = commentRepository.findById(commentId).orElseThrow();

      assertThat(comment.getMember()).isEqualTo(member);
      assertThat(comment.getPost()).isEqualTo(post);
      assertThat(comment.getParent()).isEqualTo(parent);
      assertThat(comment.getContent()).isEqualTo("테스트 댓글 내용");
    }

    @Test
    @DisplayName("부모 댓글의 parent는 null로 저장된다")
    public void 부모_댓글의_parent는_null로_저장된다() throws Exception {
      long commentId = commentService.create(member, post.getId(), null, "테스트 댓글 내용");

      Comment comment = commentRepository.findById(commentId).orElseThrow();

      assertThat(comment.getMember()).isEqualTo(member);
      assertThat(comment.getPost()).isEqualTo(post);
      assertThat(comment.getParent()).isEqualTo(null);
      assertThat(comment.getContent()).isEqualTo("테스트 댓글 내용");
    }

    @Test
    @DisplayName("대댓글에 댓글을 달면 댓글 생성은 실패한다.")
    public void 대댓글에_댓글을_달면_댓글_생성은_실패한다() throws Exception {
      Comment comment = commentTestHelper.builder().parent(parent).build();
      long postId = post.getId();
      long commentId = comment.getId();
      em.flush();
      em.clear();

      assertThrows(BusinessException.class, () -> {
        commentService.create(member, postId, commentId, "테스트 댓글 내용");
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
    @DisplayName("댓글 삭제 시 댓글의 좋아요와 싫어요는 삭제된다.")
    public void 댓글_삭제_시_댓글의_좋아요와_싫어요는_삭제된다() throws Exception {
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
    @DisplayName("댓글 삭제 시 댓글 작성자가 Virtual Member로 변경된다.")
    public void 댓글_삭제_시_댓글_작성자와_Virtual_Member로_변경된다() throws Exception {
      commentService.delete(member, comment.getId());

      Member virtualMember = memberFindService.getVirtualMember();
      assertThat(comment.getMember().getRealName()).isEqualTo(virtualMember.getRealName());
      assertThat(comment.getContent()).isEqualTo(DELETED_COMMENT_CONTENT);
    }
  }
}
