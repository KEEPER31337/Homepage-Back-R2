package com.keeper.homepage.domain.post.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.post.MemberHasPostDislike;
import com.keeper.homepage.domain.member.entity.post.MemberHasPostLike;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.comment.entity.Comment;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class PostRepositoryTest extends IntegrationTest {

  private Member member;
  private Post post;
  private Thumbnail thumbnail;

  @BeforeEach
  void setUp() {
    member = memberTestHelper.generate();
    post = postTestHelper.generate();
    thumbnail = thumbnailTestHelper.generateThumbnail();
  }

  @Nested
  @DisplayName("Post Save 테스트")
  class PostSaveTest {

    @Test
    @DisplayName("회원의 게시글 좋아요와 싫어요는 DB에 저장되어야 한다.")
    void should_saveLikeAndDislike_when_memberLikeAndDislike() {
      member.like(post);
      member.dislike(post);

      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).orElseThrow();
      post = postRepository.findById(post.getId()).orElseThrow();
      var findLike = memberHasPostLikeRepository.findAllByPost(post).get(0);
      var findDislike = memberHasPostDislikeRepository.findAllByPost(post).get(0);

      assertThat(findLike.getMember()).isEqualTo(member);
      assertThat(findLike.getPost()).isEqualTo(post);
      assertThat(findDislike.getMember()).isEqualTo(member);
      assertThat(findDislike.getPost()).isEqualTo(post);
    }

    @Test
    @DisplayName("좋아요를 하면 포스팅의 좋아요 리스트에도 좋아요 정보가 있어야 한다.")
    void should_existPostLike_when_memberLike() {
      member.like(post);

      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).orElseThrow();
      post = postRepository.findById(post.getId()).orElseThrow();

      assertThat(post.getPostLikes()).hasSize(1);
      assertThat(post.getPostLikes()).contains(MemberHasPostLike.builder()
          .member(member)
          .post(post)
          .build());
    }
  }

  @Nested
  @DisplayName("Post Remove 테스트")
  class PostRemoveTest {

    @Test
    @DisplayName("포스팅을 지우면 썸네일도 함께 지워진다.")
    void should_deletedThumbnail_when_deletePost() {
      Post postWithThumbnail = postTestHelper.builder()
          .thumbnail(thumbnail)
          .build();

      postRepository.delete(postWithThumbnail);

      em.flush();
      em.clear();
      assertThat(thumbnailRepository.findAll()).doesNotContain(thumbnail);
    }

    @Test
    @DisplayName("포스팅을 지우면 파일들도 함께 지워진다.")
    void should_deletedFiles_when_deletePost() {
      FileEntity file = fileUtil.saveFile(thumbnailTestHelper.getThumbnailFile()).orElseThrow();
      post.addFile(file);

      postRepository.delete(post);

      em.flush();
      em.clear();
      assertThat(fileRepository.findAll()).doesNotContain(file);
    }

    @Test
    @DisplayName("포스팅을 지우면 댓글들도 함께 지워진다.")
    void should_deletedComments_when_deletePost() {
      Comment comment = commentTestHelper.generate();
      post.addComment(comment);

      postRepository.delete(post);

      em.flush();
      em.clear();
      assertThat(commentRepository.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("포스팅을 지우면 회원의 좋아요, 싫어요들도 함께 지워진다.")
    void should_deletedLikeAndDislike_when_deletePost() {
      member.like(post);
      member.dislike(post);

      em.flush();
      em.clear();
      post = postRepository.findById(post.getId()).orElseThrow();
      postRepository.delete(post);

      em.flush();
      em.clear();
      assertThat(memberHasPostLikeRepository.findAll()).hasSize(0);
      assertThat(memberHasPostDislikeRepository.findAll()).hasSize(0);
    }
  }

  @Nested
  @DisplayName("게시글 좋아요 싫어요 테스트")
  class PostLikeDislikeTest {

    @Test
    @DisplayName("게시글을 중복으로 좋아요 싫어요 했을 때 중복된 값이 DB에 저장되지 않는다.")
    void should_nothingDuplicateLikeDislike_when_duplicateLikeDislike() {
      member.like(post);
      member.like(post);
      member.like(post);
      member.dislike(post);
      member.dislike(post);
      member.dislike(post);

      em.flush();
      em.clear();
      List<MemberHasPostLike> postLikes = memberHasPostLikeRepository.findAll();
      List<MemberHasPostDislike> postDislikes = memberHasPostDislikeRepository.findAll();

      assertThat(postLikes).hasSize(1);
      assertThat(postDislikes).hasSize(1);
    }

    @Test
    @DisplayName("좋아요를 취소하면 DB에서 데이터가 삭제되어야 한다.")
    void should_deletedLike_when_cancelLike() {
      member.like(post);
      member.cancelLike(post);

      em.flush();
      em.clear();
      List<MemberHasPostLike> postLikes = memberHasPostLikeRepository.findAll();

      assertThat(postLikes).hasSize(0);
    }

    @Test
    @DisplayName("좋아요를 취소하면 포스팅의 좋아요 리스트에도 좋아요 정보가 삭제되어야 한다.")
    void should_deletedPostLike_when_cancelLike() {
      member.like(post);
      member.cancelLike(post);

      em.flush();
      em.clear();
      post = postRepository.findById(post.getId()).orElseThrow();

      assertThat(post.getPostLikes()).hasSize(0);
    }
  }

  @Nested
  @DisplayName("DB NOT NULL DEFAULT 테스트")
  class NotNullDefaultTest {

    @Test
    @DisplayName("DB 디폴트 처리가 있는 값을 넣지 않았을 때 DB 디폴트 값으로 처리해야 한다.")
    void should_processDefault_when_EmptyValue() {
      em.flush();
      em.clear();
      Post findPost = postRepository.findById(post.getId()).orElseThrow();

      assertThat(findPost.getVisitCount()).isEqualTo(0);
      assertThat(findPost.getLikeCount()).isEqualTo(0);
      assertThat(findPost.getDislikeCount()).isEqualTo(0);
      assertThat(findPost.getCommentCount()).isEqualTo(0);
      assertThat(findPost.getAllowComment()).isEqualTo(true);
      assertThat(findPost.getIsNotice()).isEqualTo(false);
      assertThat(findPost.getIsSecret()).isEqualTo(false);
      assertThat(findPost.getIsTemp()).isEqualTo(false);
    }
  }
}
