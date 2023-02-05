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
  private Thumbnail thumbnail;
  private FileEntity file1, file2;

  @BeforeEach
  void setUp() {
    member = memberTestHelper.generate();
    thumbnail = thumbnailTestHelper.generateThumbnail();
    file1 = fileUtil.saveFile(thumbnailTestHelper.getThumbnailFile()).orElseThrow();
    file2 = fileUtil.saveFile(thumbnailTestHelper.getThumbnailFile()).orElseThrow();
  }

  @Nested
  @DisplayName("Posting Remove 테스트")
  class PostingRemoveTest {

    @Test
    @DisplayName("포스팅을 지우면 썸네일도 함께 지워진다")
    void should_deletedThumbnail_when_deletePosting() {
      Posting posting = postingTestHelper.builder()
          .thumbnail(thumbnail)
          .build();

      postingRepository.delete(posting);

      assertThat(thumbnailRepository.findAll()).doesNotContain(thumbnail);
    }

    @Test
    @DisplayName("포스팅을 지우면 파일들도 함께 지워진다")
    void should_deletedFiles_when_deletePosting() {
      Posting posting = postingTestHelper.generate();
      posting.addFile(file1);
      posting.addFile(file2);

      postingRepository.delete(posting);

      assertThat(fileRepository.findAll()).doesNotContain(file1);
      assertThat(fileRepository.findAll()).doesNotContain(file2);
    }

    @Test
    @DisplayName("포스팅을 지우면 댓글들도 함께 지워진다")
    void should_deletedComments_when_deletePosting() {
      Posting posting = postingTestHelper.generate();
      posting.addComment(member, 0L, "댓글1", 0, 0, "0.0.0.0");
      posting.addComment(member, 0L, "댓글2", 0, 0, "0.0.0.0");

      postingRepository.delete(posting);

      assertThat(commentRepository.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("포스팅을 지우면 회원의 좋아요, 싫어요들도 함께 지워진다")
    void should_deletedLikeAndDislike_when_deletePosting() {
      Posting posting = postingTestHelper.generate();
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
    @DisplayName("게시글을 중복으로 좋아요 싫어요 했을 때 중복된 값이 DB에 저장되지 않는다")
    void should_nothingDuplicateLikeDislike_when_duplicateLikeDislike() {
      Posting posting = postingTestHelper.generate();
      posting.like(member);
      posting.like(member);
      posting.like(member);
      posting.dislike(member);
      posting.dislike(member);
      posting.dislike(member);

      List<MemberHasPostingLike> postingLikes = memberHasPostingLikeRepository.findAll();
      List<MemberHasPostingDislike> postingDislikes = memberHasPostingDislikeRepository.findAll();

      assertThat(postingLikes).hasSize(1);
      assertThat(postingDislikes).hasSize(1);
    }

    @Test
    @DisplayName("좋아요를 취소하면 DB에서 데이터가 삭제되어야 한다")
    void should_deletedLike_when_cancelLike() {
      Posting posting = postingTestHelper.generate();
      posting.like(member);
      posting.cancelLike(member);

      List<MemberHasPostingLike> postingLikes = memberHasPostingLikeRepository.findAll();

      assertThat(postingLikes).hasSize(0);
    }
  }
}
