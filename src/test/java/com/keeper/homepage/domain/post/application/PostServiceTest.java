package com.keeper.homepage.domain.post.application;

import static com.keeper.homepage.domain.post.application.PostService.EXAM_ACCESSIBLE_ATTENDANCE_COUNT;
import static com.keeper.homepage.domain.post.application.PostService.EXAM_ACCESSIBLE_COMMENT_COUNT;
import static com.keeper.homepage.domain.post.application.PostService.EXAM_ACCESSIBLE_POINT;
import static com.keeper.homepage.domain.post.entity.category.Category.DefaultCategory.ANONYMOUS_CATEGORY;
import static com.keeper.homepage.domain.post.entity.category.Category.DefaultCategory.EXAM_CATEGORY;
import static com.keeper.homepage.domain.post.entity.category.Category.DefaultCategory.VIRTUAL_CATEGORY;
import static com.keeper.homepage.domain.thumbnail.entity.Thumbnail.DefaultThumbnail.DEFAULT_POST_THUMBNAIL;
import static com.keeper.homepage.global.util.file.server.FileServerConstants.ROOT_PATH;
import static java.io.File.separator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.comment.entity.Comment;
import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.dto.response.PostResponse;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.category.Category;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.web.WebUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

public class PostServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("게시글 생성")
  class CreatePost {

    private Member member;
    private Category category;
    private MockMultipartFile thumbnail;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.generate();
      category = categoryTestHelper.generate();
      thumbnail = thumbnailTestHelper.getThumbnailFile();
    }

    @Test
    @DisplayName("게시글을 생성하면 성공적으로 저장된다.")
    void should_success_when_createPost() {
      Post post = Post.builder()
          .title("게시글 제목")
          .content("게시글 내용")
          .member(member)
          .ipAddress(WebUtil.getUserIP())
          .password("게시글 암호")
          .build();

      Long postId = postService.create(post, category.getId(), thumbnail, List.of(thumbnail));
      em.flush();
      em.clear();
      Post findPost = postRepository.findById(postId).orElseThrow();

      assertThat(findPost.getTitle()).isEqualTo("게시글 제목");
      assertThat(findPost.getContent()).isEqualTo("게시글 내용");
      assertThat(findPost.getMember()).isEqualTo(member);
      assertThat(findPost.getVisitCount()).isEqualTo(0);
      assertThat(findPost.getIpAddress()).isEqualTo(WebUtil.getUserIP());
      assertThat(findPost.getAllowComment()).isEqualTo(true);
      assertThat(findPost.getIsNotice()).isEqualTo(false);
      assertThat(findPost.getIsSecret()).isEqualTo(false);
      assertThat(findPost.getIsTemp()).isEqualTo(false);
      assertThat(findPost.getPassword()).isEqualTo("게시글 암호");
      assertThat(findPost.getCategory().getId()).isEqualTo(category.getId());
      assertThat(findPost.getThumbnail()).isNotNull();
      assertThat(findPost.getFiles()).isNotEmpty();
    }

    @Test
    @DisplayName("요청한 카테고리를 찾을 수 없는 경우 BusinessException 던진다.")
    void should_throwBusinessException_when_requestNotFoundCategory() {
      Post post = Post.builder().build();

      assertThatThrownBy(() -> postService.create(post, 0L, thumbnail, List.of(thumbnail)))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining("존재하지 않는 게시글 카테고리입니다.");
    }
  }

  @Nested
  @DisplayName("게시글 조회")
  class FindPost {

    private Member bestMember, member;
    private Category virtualCategory, examCategory;
    private Post post;
    private Thumbnail thumbnail;
    private final long virtualPostId = 1;

    private static final LocalDate now = LocalDate.now();

    @BeforeEach
    void setUp() {
      bestMember = memberTestHelper.builder().point(EXAM_ACCESSIBLE_POINT).build();
      for (int i = 0; i < EXAM_ACCESSIBLE_COMMENT_COUNT; i++) {
        commentRepository.save(commentTestHelper.builder().member(bestMember).build());
      }
      for (int i = 0; i < EXAM_ACCESSIBLE_ATTENDANCE_COUNT; i++) {
        attendanceRepository
            .save(attendanceTestHelper.builder().member(bestMember).date(now.plusDays(i)).build());
      }
      virtualCategory = categoryRepository.findById(VIRTUAL_CATEGORY.getId()).orElseThrow();
      examCategory = categoryRepository.findById(EXAM_CATEGORY.getId()).orElseThrow();
      thumbnail = thumbnailRepository.findById(DEFAULT_POST_THUMBNAIL.getId()).orElseThrow();
    }

    @Test
    @DisplayName("게시글을 조회하면 성공적으로 조회된다.")
    void should_success_when_getPost() {
      post = postTestHelper.builder()
          .member(bestMember)
          .password("비밀비밀")
          .category(virtualCategory)
          .thumbnail(thumbnail)
          .build();

      em.flush();
      em.clear();
      bestMember = memberRepository.findById(bestMember.getId()).orElseThrow();
      post = postRepository.findById(post.getId()).orElseThrow();
      PostResponse response = postService.find(bestMember, post.getId(), "비밀비밀");

      assertThat(response.getCategoryName()).isEqualTo(VIRTUAL_CATEGORY.getName());
      assertThat(response.getTitle()).isEqualTo(post.getTitle());
      assertThat(response.getWriterName()).isEqualTo(bestMember.getProfile().getNickname().get());
      assertThat(response.getRegisterTime()).isEqualTo(post.getRegisterTime());
      assertThat(response.getVisitCount()).isEqualTo(post.getVisitCount());
      assertThat(response.getThumbnailPath())
          .isEqualTo(thumbnailUtil.getThumbnailPath(DEFAULT_POST_THUMBNAIL.getPath()));
      assertThat(response.getContent()).isEqualTo(post.getContent());
    }

    @Test
    @DisplayName("내가 쓴 비밀글은 비밀번호 없이 조회 가능하다.")
    public void should_successWithOutPassword_when_writerIsMe() throws Exception {
      post = postTestHelper.builder()
          .member(bestMember)
          .isSecret(true)
          .password("비밀비밀")
          .build();

      em.flush();
      em.clear();
      bestMember = memberRepository.findById(bestMember.getId()).orElseThrow();
      post = postRepository.findById(post.getId()).orElseThrow();

      assertDoesNotThrow(() -> {
        postService.find(bestMember, post.getId(), null);
      });
    }

    @Test
    @DisplayName("익명 글은 작성자가 익명으로 조회되어야 한다.")
    public void should_getAnonymousName_when_getAnonymousPost() throws Exception {
      Category anonymousCategory = categoryRepository.findById(ANONYMOUS_CATEGORY.getId())
          .orElseThrow();
      post = postTestHelper.builder()
          .member(bestMember)
          .category(anonymousCategory)
          .thumbnail(thumbnail)
          .build();

      em.flush();
      em.clear();
      bestMember = memberRepository.findById(bestMember.getId()).orElseThrow();
      post = postRepository.findById(post.getId()).orElseThrow();

      PostResponse response = postService.find(bestMember, post.getId(), null);

      assertThat(response.getWriterName()).isEqualTo("익명");
    }

    @Test
    @DisplayName("게시글 조회수는 하루에 한번만 증가해야 한다.")
    public void should_VisitCountIncreaseOncePerDay_when_getPost() throws Exception {
      // TODO: 기능 구현 후 테스트 작성
      assertThat(true);
    }

    @Test
    @DisplayName("유효하지 않은 Post 데이터는 조회할 수 없다.")
    public void should_fail_when_getInValidPost() throws Exception {
      assertThrows(BusinessException.class, () -> {
        postService.find(bestMember, -1, null);
      });

      assertThrows(BusinessException.class, () -> {
        postService.find(bestMember, 0, null);
      });

      assertThrows(BusinessException.class, () -> {
        postService.find(bestMember, virtualPostId, null);
      });
    }

    @Test
    @DisplayName("족보 글은 포인트가 20000점 미만이면 조회할 수 없다.")
    public void should_failGetExamPost_when_pointLessThan20000() throws Exception {
      member = memberTestHelper.builder().point(0).build();
      post = postTestHelper.builder()
          .member(bestMember)
          .category(examCategory)
          .build();

      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).orElseThrow();
      post = postRepository.findById(post.getId()).orElseThrow();

      assertThrows(BusinessException.class, () -> {
        postService.find(member, post.getId(), null);
      });
    }

    @Test
    @DisplayName("족보 글은 회원의 댓글 수가 5개 미만이면 조회할 수 없다.")
    public void should_failGetExamPost_when_commentLessThan5() throws Exception {
      member = memberTestHelper.builder().point(EXAM_ACCESSIBLE_POINT).build();
      commentRepository.deleteAll();
      post = postTestHelper.builder()
          .member(bestMember)
          .category(examCategory)
          .build();

      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).orElseThrow();
      post = postRepository.findById(post.getId()).orElseThrow();

      assertThrows(BusinessException.class, () -> {
        postService.find(member, post.getId(), null);
      });
    }

    @Test
    @DisplayName("족보 글은 회원의 출석 수가 10회 미만이면 조회할 수 없다.")
    public void should_failGetExamPost_when_attendanceLessThan10() throws Exception {
      member = memberTestHelper.builder().point(EXAM_ACCESSIBLE_POINT).build();
      attendanceRepository.deleteAll();
      post = postTestHelper.builder()
          .member(bestMember)
          .category(examCategory)
          .build();

      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).orElseThrow();
      post = postRepository.findById(post.getId()).orElseThrow();

      assertThrows(BusinessException.class, () -> {
        postService.find(member, post.getId(), null);
      });
    }

    @Test
    @DisplayName("임시 저장글은 내가 쓴 게 아니면 조회할 수 없다.")
    public void should_failGetTempPost_when_writerIsNotMe() throws Exception {
      member = memberTestHelper.generate();
      post = postTestHelper.builder()
          .member(member)
          .isTemp(true)
          .build();

      em.flush();
      em.clear();
      bestMember = memberRepository.findById(bestMember.getId()).orElseThrow();
      post = postRepository.findById(post.getId()).orElseThrow();

      assertThrows(BusinessException.class, () -> {
        postService.find(bestMember, post.getId(), null);
      });
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 비밀글은 조회할 수 없다.")
    public void should_fail_when_wrongPassword() throws Exception {
      member = memberTestHelper.builder().build();
      post = postTestHelper.builder()
          .member(member)
          .isSecret(true)
          .password("비밀비밀")
          .build();

      em.flush();
      em.clear();
      bestMember = memberRepository.findById(bestMember.getId()).orElseThrow();
      post = postRepository.findById(post.getId()).orElseThrow();

      assertThrows(BusinessException.class, () -> {
        postService.find(bestMember, post.getId(), "틀림");
      });

      assertThrows(BusinessException.class, () -> {
        postService.find(bestMember, post.getId(), null);
      });
    }
  }

  @Nested
  @DisplayName("게시글 수정")
  class UpdatePost {

    private Member member;
    private Category category;
    private Post post;
    private MockMultipartFile thumbnail, file1, file2;

    @BeforeEach
    void setUp() throws IOException {
      member = memberTestHelper.builder().point(EXAM_ACCESSIBLE_POINT).build();
      category = categoryTestHelper.generate();
      thumbnail = thumbnailTestHelper.getSmallThumbnailFile();
      file1 = new MockMultipartFile("file", "testImage_1x1.png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_1x1.png"));
      file2 = new MockMultipartFile("file",
          "testImage_210x210.png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_210x210.png"));
      post = Post.builder()
          .title("게시글 제목")
          .content("게시글 내용")
          .member(member)
          .ipAddress(WebUtil.getUserIP())
          .build();
    }

    @Test
    @DisplayName("내가 작성한 게시글인 경우 게시글 수정은 성공한다.")
    public void should_success_when_writerIsMe() throws Exception {
      long postId = postTestHelper.builder().member(member).build().getId();

      Post newPost = Post.builder()
          .title("수정 제목")
          .content("수정 내용")
          .build();

      assertDoesNotThrow(() -> {
        postService.update(member, postId, newPost, null, null);
      });
    }

    @Test
    @DisplayName("게시글 썸네일 수정은 성공한다.")
    public void should_success_when_updateThumbnail() throws Exception {
      long postId = postService.create(post, category.getId(), thumbnail, null);
      Thumbnail oldThumbnail = post.getThumbnail();

      Post newPost = Post.builder()
          .title("수정 제목")
          .content("수정 내용")
          .build();

      postService.update(member, postId, newPost, thumbnailTestHelper.getThumbnailFile(), null);
      Thumbnail newThumbnail = post.getThumbnail();

      checkDoesNotExist(oldThumbnail);
      checkExist(newThumbnail);
    }

    private void checkDoesNotExist(Thumbnail thumbnail) {
      long thumbnailId = thumbnail.getId();
      String filePath = thumbnail.getFileEntity().getFilePath();
      Path fileFullPath = Path.of(ROOT_PATH + separator + filePath);
      Path thumbnailFullPath = Path.of(ROOT_PATH + separator + thumbnail.getPath());

      assertThat(Files.exists(fileFullPath)).isFalse();
      assertThat(Files.exists(thumbnailFullPath)).isFalse();
      assertThat(thumbnailRepository.findById(thumbnailId)).isEmpty();
    }

    private void checkExist(Thumbnail thumbnail) {
      long thumbnailId = post.getThumbnail().getId();
      String filePath = thumbnail.getFileEntity().getFilePath();
      Path fileFullPath = Path.of(ROOT_PATH + separator + filePath);
      Path thumbnailFullPath = Path.of(ROOT_PATH + separator + thumbnail.getPath());

      assertThat(Files.exists(fileFullPath)).isTrue();
      assertThat(Files.exists(thumbnailFullPath)).isTrue();
      assertThat(thumbnailRepository.findById(thumbnailId)).isNotEmpty();
    }

    @Test
    @DisplayName("게시글 파일 수정은 성공한다.")
    public void should_success_when_updateFiles() throws Exception {
      long postId = postService.create(post, category.getId(), null, List.of(file1));
      List<FileEntity> beforeFiles = fileRepository.findAllByPost(post);

      Post newPost = Post.builder()
          .title("수정 제목")
          .content("수정 내용")
          .ipAddress(WebUtil.getUserIP())
          .build();

      postService.update(member, postId, newPost, null, List.of(file2));
      List<FileEntity> afterFiles = fileRepository.findAllByPost(post);

      for (FileEntity fileEntity : beforeFiles) {
        assertThat(new File(fileEntity.getFilePath())).doesNotExist();
        assertThat(fileRepository.findById(fileEntity.getId())).isEmpty();
      }
      for (FileEntity fileEntity : afterFiles) {
        assertThat(new File(fileEntity.getFilePath())).exists();
        assertThat(fileRepository.findById(fileEntity.getId())).isNotEmpty();
      }
    }

    @Test
    @DisplayName("비밀 게시글로 설정한 경우 패스워드가 없으면 게시글 수정은 실패한다.")
    public void should_fail_when_secretPostWithoutPassword() throws Exception {
      long postId = postTestHelper.builder().member(member).build().getId();

      Post newPost = Post.builder()
          .title("수정 제목")
          .content("수정 내용")
          .isSecret(true)
          .build();

      assertThrows(BusinessException.class, () -> {
        postService.update(member, postId, newPost, null, null);
      });
    }
  }

  @Nested
  @DisplayName("게시글 삭제")
  class DeletePost {

    private Post post;
    private long postId;
    private Member member;

    @BeforeEach
    void setUp() throws IOException {
      member = memberTestHelper.generate();
      post = postTestHelper.builder().member(member).build();
      postId = post.getId();
      member.like(post);
      member.dislike(post);
      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).orElseThrow();
    }

    @Test
    @DisplayName("게시글 삭제는 성공한다.")
    public void 게시글_삭제는_성공한다() throws Exception {
      postService.delete(member, postId);

      em.flush();
      em.clear();
      assertThat(postRepository.findById(postId)).isEmpty();
    }

    @Test
    @DisplayName("게시글 삭제 시 좋아요 싫어요도 삭제된다.")
    public void 게시글_삭제_시_좋아요_싫어요도_삭제된다() throws Exception {
      postService.delete(member, postId);

      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).orElseThrow();
      assertThat(member.getPostLikes()).isEmpty();
      assertThat(member.getPostDislikes()).isEmpty();
    }

    @Test
    @DisplayName("게시글 삭제 시 댓글과 댓글의 좋아요 싫어요들도 삭제된다.")
    public void 게시글_삭제_시_댓글과_댓글의_좋아요_싫어요들도_삭제된다() throws Exception {
      post = postRepository.findById(postId).orElseThrow();
      Comment comment = commentTestHelper.builder().post(post).member(member).build();
      long commentId = comment.getId();
      member.like(comment);
      member.dislike(comment);

      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).orElseThrow();
      postService.delete(member, postId);

      em.flush();
      em.clear();
      member = memberRepository.findById(member.getId()).orElseThrow();
      assertThat(commentRepository.findById(commentId)).isEmpty();
      assertThat(member.getCommentLikes()).isEmpty();
    }
  }

  @Nested
  @DisplayName("게시글 좋아요 싫어요")
  class LikeDislikePost {

    private Post post;
    private long postId;
    private Member member;

    @BeforeEach
    void setUp() throws IOException {
      post = postTestHelper.generate();
      postId = post.getId();
      member = memberTestHelper.generate();
    }

    @Test
    @DisplayName("게시글 좋아요 싫어요는 성공한다.")
    public void 게시글_좋아요_싫어요는_성공한다() throws Exception {
      assertDoesNotThrow(() -> {
        postService.like(member, postId);
      });
      assertDoesNotThrow(() -> {
        postService.dislike(member, postId);
      });

      assertThat(member.isLike(post)).isTrue();
      assertThat(member.isDislike(post)).isTrue();
    }

    @Test
    @DisplayName("게시글 좋아요 싫어요 취소는 성공한다.")
    public void 게시글_좋아요_싫어요_취소는_성공한다() throws Exception {
      postService.like(member, postId);
      postService.dislike(member, postId);

      assertDoesNotThrow(() -> {
        postService.like(member, postId);
      });
      assertDoesNotThrow(() -> {
        postService.dislike(member, postId);
      });

      assertThat(member.isLike(post)).isFalse();
      assertThat(member.isDislike(post)).isFalse();
    }
  }
}
