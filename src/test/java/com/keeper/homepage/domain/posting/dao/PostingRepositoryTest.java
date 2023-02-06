package com.keeper.homepage.domain.posting.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.posting.MemberHasPostingDislike;
import com.keeper.homepage.domain.member.entity.posting.MemberHasPostingLike;
import com.keeper.homepage.domain.posting.entity.Posting;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class PostingRepositoryTest extends IntegrationTest {

  private Member member;
  private Posting posting;
  private Thumbnail thumbnail;

  @BeforeEach
  void setUp() {
    member = memberTestHelper.generate();
    posting = postingTestHelper.generate();
    thumbnail = thumbnailTestHelper.generateThumbnail();
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
      FileEntity file1 = fileTestHelper.generateTestFile();
      FileEntity file2 = fileTestHelper.generateTestFile();
      posting.addFile(file1);
      posting.addFile(file2);

      postingRepository.delete(posting);

      assertThat(fileRepository.findAll()).doesNotContain(file1);
      assertThat(fileRepository.findAll()).doesNotContain(file2);
    }

    @Test
    @DisplayName("포스팅을 지우면 댓글들도 함께 지워진다.")
    void should_deletedComments_when_deletePosting() {
      posting.addComment(commentTestHelper.generateComment());
      posting.addComment(commentTestHelper.generateComment());

      postingRepository.delete(posting);

      assertThat(commentRepository.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("포스팅을 지우면 회원의 좋아요, 싫어요들도 함께 지워진다.")
    void should_deletedLikeAndDislike_when_deletePosting() {
      posting.like(member);
      posting.dislike(member);

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
      posting.like(member);
      posting.like(member);
      posting.like(member);
      posting.dislike(member);
      posting.dislike(member);
      posting.dislike(member);

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
      posting.like(member);
      posting.cancelLike(member);

      em.flush();
      em.clear();
      List<MemberHasPostingLike> postingLikes = memberHasPostingLikeRepository.findAll();

      assertThat(postingLikes).hasSize(0);
    }
  }

  @Nested
  @DisplayName("DB NOT NULL DEFAULT 테스트")
  class NotNullDefaultTest {

    @Test
    @DisplayName("DB 디폴트 처리가 있는 값을 넣지 않았을 때 DB 디폴트 값으로 처리해야 한다.")
    void should_process_when_EmptyValue() {
      em.flush();
      em.clear();

      Posting findPosting = postingRepository.findById(posting.getId()).orElseThrow();

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
