package com.keeper.homepage.domain.comment.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.comment.MemberHasCommentDislike;
import com.keeper.homepage.domain.member.entity.comment.MemberHasCommentLike;
import com.keeper.homepage.domain.comment.entity.Comment;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CommentRepositoryTest extends IntegrationTest {

  private Member member;
  private Comment comment;

  @BeforeEach
  void setUp() {
    member = memberTestHelper.generate();
    comment = Comment.builder()
        .member(member)
        .posting(postingTestHelper.generate())
        .parentCommentId(0L)
        .content("댓글내용")
        .ipAddress("0.0.0.0")
        .build();
  }

  @Nested
  @DisplayName("Comment Save 테스트")
  class CommentSaveTest{

    @Test
    @DisplayName("부모 댓글에 자식 댓글을 등록하면 DB에 저장되어야 한다.")
    void should_saveChildComment_when_parentAddChild() {
      // TODO: DB가 변경되면 성공해야 합니다.
    }
  }

  @Nested
  @DisplayName("Comment Remove 테스트")
  class CommentRemoveTest {

    @Test
    @DisplayName("댓글을 지우면 댓글의 좋아요, 싫어요가 지워진다.")
    void should_deletedLikeAndDislike_when_deleteComment() {
      member.like(comment);
      member.dislike(comment);

      em.flush();
      em.clear();
      commentRepository.delete(comment);

      assertThat(memberHasCommentLikeRepository.findAll()).hasSize(0);
      assertThat(memberHasCommentDislikeRepository.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("부모 댓글을 지워도 자식 댓글은 삭제되지 않아야 한다.")
    void should_noDeleteChildren_when_deleteParentComment() {
      // TODO: DB가 변경되면 성공해야 합니다.
    }
  }

  @Nested
  @DisplayName("댓글 좋아요 싫어요 테스트")
  class CommentLikeDislikeTest {

    @Test
    @DisplayName("댓글을 중복으로 좋아요 싫어요 했을 때 중복된 값이 DB에 저장되지 않는다.")
    void should_nothingDuplicateLikeAndDislike_when_duplicateLikeAndDislike() {
      member.like(comment);
      member.like(comment);
      member.like(comment);
      member.dislike(comment);
      member.dislike(comment);
      member.dislike(comment);

      em.flush();
      em.clear();
      List<MemberHasCommentLike> commentLikes = memberHasCommentLikeRepository.findAll();
      List<MemberHasCommentDislike> commentDislikes = memberHasCommentDislikeRepository.findAll();

      assertThat(commentLikes).hasSize(1);
      assertThat(commentDislikes).hasSize(1);
    }

    @Test
    @DisplayName("좋아요를 취소하면 DB에서 데이터가 삭제되어야 한다.")
    void should_deletedLike_when_cancelLike() {
      member.like(comment);
      member.cancelLike(comment);

      em.flush();
      em.clear();
      List<MemberHasCommentLike> commentLikes = memberHasCommentLikeRepository.findAll();

      assertThat(commentLikes).hasSize(0);
    }

    @Test
    @DisplayName("좋아요를 취소하면 댓글의 좋아요 리스트에도 좋아요 정보가 삭제되어야 한다.")
    void should_deletedCommentLike_when_cancelLike() {
      member.like(comment);
      member.cancelLike(comment);

      em.flush();
      em.clear();

      assertThat(comment.getCommentLikes()).hasSize(0);
    }
  }

  @Nested
  @DisplayName("DB NOT NULL DEFAULT 테스트")
  class NotNullDefaultTest {

    @Test
    @DisplayName("좋아요 개수, 싫어요 개수를 넣지 않았을 때 0으로 처리해야 한다.")
    void should_processDefault_when_EmptyLikeCountAndDislikeCount() {
      Comment commentBuild = commentTestHelper.generate();
      em.flush();
      em.clear();

      Comment findComment = commentRepository.findById(commentBuild.getId()).orElseThrow();

      assertThat(findComment.getLikeCount()).isEqualTo(0);
      assertThat(findComment.getDislikeCount()).isEqualTo(0);
    }
  }
}
