package com.keeper.homepage.domain.posting.dao.comment;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.comment.MemberHasCommentDislike;
import com.keeper.homepage.domain.member.entity.comment.MemberHasCommentLike;
import com.keeper.homepage.domain.posting.entity.comment.Comment;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CommentRepositoryTest extends IntegrationTest {

  private Member member;

  @BeforeEach
  void setUp() {
    member = memberTestHelper.generate();
  }

  @Nested
  @DisplayName("Comment Remove 테스트")
  class CommentRemoveTest {

    @Test
    @DisplayName("댓글을 지우면 댓글의 좋아요, 싫어요가 지워진다")
    void should_deletedLikeAndDislike_when_deleteComment() {
      Comment comment = commentTestHelper.generate();
      comment.like(member);
      comment.dislike(member);

      commentRepository.delete(comment);

      assertThat(memberHasCommentLikeRepository.findAll()).hasSize(0);
      assertThat(memberHasCommentDislikeRepository.findAll()).hasSize(0);
    }
  }

  @Nested
  @DisplayName("댓글 좋아요 싫어요 테스트")
  class CommentLikeDislikeTest{

    @Test
    @DisplayName("댓글을 중복으로 좋아요 싫어요 했을 때 중복된 값이 DB에 저장되지 않는다")
    void should_nothingDuplicateLikeDislike_when_duplicateLikeDislike() {
      Comment comment = commentTestHelper.generate();
      comment.like(member);
      comment.like(member);
      comment.like(member);
      comment.dislike(member);
      comment.dislike(member);
      comment.dislike(member);

      List<MemberHasCommentLike> commentLikes = memberHasCommentLikeRepository.findAll();
      List<MemberHasCommentDislike> commentDislikes = memberHasCommentDislikeRepository.findAll();

      assertThat(commentLikes).hasSize(1);
      assertThat(commentDislikes).hasSize(1);
    }

    @Test
    @DisplayName("좋아요를 취소하면 DB에서 데이터가 삭제되어야 한다")
    void should_deletedLike_when_cancelLike() {
      Comment comment = commentTestHelper.generate();
      comment.like(member);
      comment.cancelLike(member);

      List<MemberHasCommentLike> commentLikes = memberHasCommentLikeRepository.findAll();

      assertThat(commentLikes).hasSize(0);
    }
  }
}
