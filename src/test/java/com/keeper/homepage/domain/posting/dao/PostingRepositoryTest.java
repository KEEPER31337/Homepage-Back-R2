package com.keeper.homepage.domain.posting.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.posting.MemberHasPostingDislike;
import com.keeper.homepage.domain.member.entity.posting.MemberHasPostingLike;
import com.keeper.homepage.domain.posting.entity.Posting;
import com.keeper.homepage.domain.posting.entity.category.Category;
import com.keeper.homepage.domain.comment.entity.Comment;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class PostingRepositoryTest extends IntegrationTest {

  private Member member;
  private Posting posting;
  private Category category;
  private Thumbnail thumbnail;

  @BeforeEach
  void setUp() {
    member = memberTestHelper.generate();
    category = categoryTestHelper.generate();
    posting = Posting.builder()
        .title("포스팅 제목")
        .content("포스팅 내용")
        .member(member)
        .ipAddress("0.0.0.0")
        .category(category)
        .build();
    thumbnail = thumbnailTestHelper.generateThumbnail();
  }

  @Nested
  @DisplayName("Posting Save 테스트")
  class PostingSaveTest {

    @Test
    @DisplayName("카테고리에 포스팅을 등록하면, 포스팅이 저장되어야 한다.")
    void should_savePosing_when_CategoryAddPosting() {
      category.addPosting(posting);

      em.flush();
      em.clear();
      Posting findPosting = postingRepository.findById(posting.getId()).orElseThrow();

      assertThat(findPosting.getId()).isEqualTo(posting.getId());
      assertThat(findPosting.getTitle()).isEqualTo(posting.getTitle());
      assertThat(findPosting.getContent()).isEqualTo(posting.getContent());
      assertThat(findPosting.getMember()).isEqualTo(posting.getMember());
      assertThat(findPosting.getCategory().getId()).isEqualTo(posting.getCategory().getId());
    }

    @Test
    @DisplayName("회원의 게시글 좋아요와 싫어요는 DB에 저장되어야 한다.")
    void should_saveLikeAndDislike_when_memberLikeAndDislike() {
      member.like(posting);
      member.dislike(posting);

      em.flush();
      em.clear();
      var findLike = memberHasPostingLikeRepository.findAllByPosting(posting).get(0);
      var findDislike = memberHasPostingDislikeRepository.findAllByPosting(posting).get(0);

      assertThat(findLike.getMember()).isEqualTo(member);
      assertThat(findLike.getPosting()).isEqualTo(posting);
      assertThat(findDislike.getMember()).isEqualTo(member);
      assertThat(findDislike.getPosting()).isEqualTo(posting);
    }

    @Test
    @DisplayName("좋아요를 하면 포스팅의 좋아요 리스트에도 좋아요 정보가 있어야 한다.")
    void should_existPostingLike_when_memberLike() {
      member.like(posting);

      em.flush();
      em.clear();

      assertThat(posting.getPostingLikes()).hasSize(1);
      assertThat(posting.getPostingLikes()).contains(MemberHasPostingLike.builder()
          .member(member)
          .posting(posting)
          .build());
    }
  }

  @Nested
  @DisplayName("Posting Remove 테스트")
  class PostingRemoveTest {

    @Test
    @DisplayName("포스팅을 지우면 썸네일도 함께 지워진다.")
    void should_deletedThumbnail_when_deletePosting() {
      Posting postingWithThumbnail = postingTestHelper.builder()
          .thumbnail(thumbnail)
          .build();

      postingRepository.delete(postingWithThumbnail);

      assertThat(thumbnailRepository.findAll()).doesNotContain(thumbnail);
    }

    @Test
    @DisplayName("포스팅을 지우면 파일들도 함께 지워진다.")
    void should_deletedFiles_when_deletePosting() {
      Posting postingBuild = postingTestHelper.generate();
      FileEntity file = fileUtil.saveFile(thumbnailTestHelper.getThumbnailFile()).orElseThrow();
      postingBuild.addFile(file);

      em.flush();
      em.clear();
      postingRepository.delete(postingBuild);

      assertThat(fileRepository.findAll()).doesNotContain(file);
    }

    @Test
    @DisplayName("포스팅을 지우면 댓글들도 함께 지워진다.")
    void should_deletedComments_when_deletePosting() {
      Comment comment = Comment.builder()
          .member(member)
          .parentCommentId(0L)
          .content("댓글 내용")
          .ipAddress("0.0.0.0")
          .build();
      posting.addComment(comment);

      em.flush();
      em.clear();
      postingRepository.delete(posting);

      assertThat(commentRepository.findAll()).hasSize(0);
      assertThat(commentRepository.findAll()).doesNotContain(comment);
    }

    @Test
    @DisplayName("포스팅을 지우면 회원의 좋아요, 싫어요들도 함께 지워진다.")
    void should_deletedLikeAndDislike_when_deletePosting() {
      member.like(posting);
      member.dislike(posting);

      em.flush();
      em.clear();
      postingRepository.delete(posting);

      assertThat(memberHasPostingLikeRepository.findAll()).hasSize(0);
      assertThat(memberHasPostingDislikeRepository.findAll()).hasSize(0);
    }
  }

  @Nested
  @DisplayName("게시글 좋아요 싫어요 테스트")
  class PostingLikeDislikeTest {

    @Test
    @DisplayName("게시글을 중복으로 좋아요 싫어요 했을 때 중복된 값이 DB에 저장되지 않는다.")
    void should_nothingDuplicateLikeDislike_when_duplicateLikeDislike() {
      member.like(posting);
      member.like(posting);
      member.like(posting);
      member.dislike(posting);
      member.dislike(posting);
      member.dislike(posting);

      em.flush();
      em.clear();
      List<MemberHasPostingLike> postingLikes = memberHasPostingLikeRepository.findAll();
      List<MemberHasPostingDislike> postingDislikes = memberHasPostingDislikeRepository.findAll();

      assertThat(postingLikes).hasSize(1);
      assertThat(postingDislikes).hasSize(1);
    }

    @Test
    @DisplayName("좋아요를 취소하면 DB에서 데이터가 삭제되어야 한다.")
    void should_deletedLike_when_cancelLike() {
      member.like(posting);
      member.cancelLike(posting);

      em.flush();
      em.clear();
      List<MemberHasPostingLike> postingLikes = memberHasPostingLikeRepository.findAll();

      assertThat(postingLikes).hasSize(0);
    }

    @Test
    @DisplayName("좋아요를 취소하면 포스팅의 좋아요 리스트에도 좋아요 정보가 삭제되어야 한다.")
    void should_deletedPostingLike_when_cancelLike() {
      member.like(posting);
      member.cancelLike(posting);

      em.flush();
      em.clear();

      assertThat(posting.getPostingLikes()).hasSize(0);
    }
  }

  @Nested
  @DisplayName("DB NOT NULL DEFAULT 테스트")
  class NotNullDefaultTest {

    @Test
    @DisplayName("DB 디폴트 처리가 있는 값을 넣지 않았을 때 DB 디폴트 값으로 처리해야 한다.")
    void should_processDefault_when_EmptyValue() {
      Posting postingBuild = postingTestHelper.generate();
      em.flush();
      em.clear();
      Posting findPosting = postingRepository.findById(postingBuild.getId()).orElseThrow();

      assertThat(findPosting.getVisitCount()).isEqualTo(0);
      assertThat(findPosting.getLikeCount()).isEqualTo(0);
      assertThat(findPosting.getDislikeCount()).isEqualTo(0);
      assertThat(findPosting.getCommentCount()).isEqualTo(0);
      assertThat(findPosting.getAllowComment()).isEqualTo(true);
      assertThat(findPosting.getIsNotice()).isEqualTo(false);
      assertThat(findPosting.getIsSecret()).isEqualTo(false);
      assertThat(findPosting.getIsTemp()).isEqualTo(false);
    }
  }
}
