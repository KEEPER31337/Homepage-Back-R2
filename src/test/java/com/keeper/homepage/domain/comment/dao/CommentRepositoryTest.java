package com.keeper.homepage.domain.comment.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.comment.entity.Comment;
import com.keeper.homepage.domain.member.entity.Member;
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
    comment = commentTestHelper.generate();
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
      member = memberRepository.findById(member.getId()).orElseThrow();

      assertThat(member.getCommentLikes()).hasSize(1);
      assertThat(member.getCommentDislikes()).hasSize(1);
    }

    @Test
    @DisplayName("좋아요를 취소하면 DB에서 데이터가 삭제되어야 한다.")
    void should_deletedLike_when_cancelLike() {
      member.like(comment);
      member.cancelLike(comment);

      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).orElseThrow();

      assertThat(member.getCommentLikes()).hasSize(0);
    }
  }
}
