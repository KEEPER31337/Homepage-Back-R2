package com.keeper.homepage.domain.post.application;

import static com.keeper.homepage.domain.post.application.PostService.EXAM_ACCESSIBLE_ATTENDANCE_COUNT;
import static com.keeper.homepage.domain.post.application.PostService.EXAM_ACCESSIBLE_COMMENT_COUNT;
import static com.keeper.homepage.domain.post.application.PostService.EXAM_ACCESSIBLE_POINT;
import static com.keeper.homepage.domain.post.entity.category.Category.DefaultCategory.ANONYMOUS_CATEGORY;
import static com.keeper.homepage.domain.post.entity.category.Category.DefaultCategory.EXAM_CATEGORY;
import static com.keeper.homepage.domain.post.entity.category.Category.DefaultCategory.VIRTUAL_CATEGORY;
import static com.keeper.homepage.domain.thumbnail.entity.Thumbnail.DefaultThumbnail.POST_THUMBNAIL;
import static com.keeper.homepage.global.util.file.server.FileServerConstants.ROOT_PATH;
import static java.io.File.separator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.post.MemberHasPostLike;
import com.keeper.homepage.domain.post.dto.request.PostUpdateRequest;
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
  @DisplayName("????????? ??????")
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
    @DisplayName("???????????? ???????????? ??????????????? ????????????.")
    void should_success_when_createPost() {
      Post post = Post.builder()
          .title("????????? ??????")
          .content("????????? ??????")
          .member(member)
          .ipAddress(WebUtil.getUserIP())
          .password("????????? ??????")
          .build();

      Long postId = postService.create(post, category.getId(), thumbnail, List.of(thumbnail));
      em.flush();
      em.clear();
      Post findPost = postRepository.findById(postId).orElseThrow();

      assertThat(findPost.getTitle()).isEqualTo("????????? ??????");
      assertThat(findPost.getContent()).isEqualTo("????????? ??????");
      assertThat(findPost.getMember()).isEqualTo(member);
      assertThat(findPost.getVisitCount()).isEqualTo(0);
      assertThat(findPost.getLikeCount()).isEqualTo(0);
      assertThat(findPost.getDislikeCount()).isEqualTo(0);
      assertThat(findPost.getCommentCount()).isEqualTo(0);
      assertThat(findPost.getIpAddress()).isEqualTo(WebUtil.getUserIP());
      assertThat(findPost.getAllowComment()).isEqualTo(true);
      assertThat(findPost.getIsNotice()).isEqualTo(false);
      assertThat(findPost.getIsSecret()).isEqualTo(false);
      assertThat(findPost.getIsTemp()).isEqualTo(false);
      assertThat(findPost.getPassword()).isEqualTo("????????? ??????");
      assertThat(findPost.getCategory().getId()).isEqualTo(category.getId());
      assertThat(findPost.getThumbnail()).isNotNull();
      assertThat(findPost.getFiles()).isNotEmpty();
    }

    @Test
    @DisplayName("????????? ??????????????? ?????? ??? ?????? ?????? BusinessException ?????????.")
    void should_throwBusinessException_when_requestNotFoundCategory() {
      Post post = Post.builder().build();

      assertThatThrownBy(() -> postService.create(post, 0L, thumbnail, List.of(thumbnail)))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining("???????????? ?????? ????????? ?????????????????????.");
    }
  }

  @Nested
  @DisplayName("????????? ??????")
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
      thumbnail = thumbnailRepository.findById(POST_THUMBNAIL.getId()).orElseThrow();
    }

    @Test
    @DisplayName("???????????? ???????????? ??????????????? ????????????.")
    void should_success_when_getPost() {
      post = postTestHelper.builder()
          .member(bestMember)
          .password("????????????")
          .category(virtualCategory)
          .thumbnail(thumbnail)
          .build();

      em.flush();
      em.clear();
      bestMember = memberRepository.findById(bestMember.getId()).orElseThrow();
      post = postRepository.findById(post.getId()).orElseThrow();
      PostResponse response = postService.find(bestMember, post.getId(), "????????????");

      assertThat(response.getCategoryName()).isEqualTo(VIRTUAL_CATEGORY.getName());
      assertThat(response.getTitle()).isEqualTo(post.getTitle());
      assertThat(response.getWriterName()).isEqualTo(bestMember.getProfile().getNickname().get());
      assertThat(response.getRegisterTime()).isEqualTo(post.getRegisterTime());
      assertThat(response.getVisitCount()).isEqualTo(post.getVisitCount());
      assertThat(response.getThumbnailPath())
          .isEqualTo(thumbnailUtil.getThumbnailPath(POST_THUMBNAIL.getPath()));
      assertThat(response.getContent()).isEqualTo(post.getContent());
      assertThat(response.getLikeCount()).isEqualTo(post.getLikeCount());
      assertThat(response.getDislikeCount()).isEqualTo(post.getDislikeCount());
    }

    @Test
    @DisplayName("?????? ??? ???????????? ???????????? ?????? ?????? ????????????.")
    public void should_successWithOutPassword_when_writerIsMe() throws Exception {
      post = postTestHelper.builder()
          .member(bestMember)
          .isSecret(true)
          .password("????????????")
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
    @DisplayName("?????? ?????? ???????????? ???????????? ??????????????? ??????.")
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

      assertThat(response.getWriterName()).isEqualTo("??????");
    }

    @Test
    @DisplayName("????????? ???????????? ????????? ????????? ???????????? ??????.")
    public void should_VisitCountIncreaseOncePerDay_when_getPost() throws Exception {
      // TODO: ?????? ?????? ??? ????????? ??????
      assertThat(true);
    }

    @Test
    @DisplayName("???????????? ?????? Post ???????????? ????????? ??? ??????.")
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
    @DisplayName("?????? ?????? ???????????? 20000??? ???????????? ????????? ??? ??????.")
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
    @DisplayName("?????? ?????? ????????? ?????? ?????? 5??? ???????????? ????????? ??? ??????.")
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
    @DisplayName("?????? ?????? ????????? ?????? ?????? 10??? ???????????? ????????? ??? ??????.")
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
    @DisplayName("?????? ???????????? ?????? ??? ??? ????????? ????????? ??? ??????.")
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
    @DisplayName("??????????????? ???????????? ????????? ???????????? ????????? ??? ??????.")
    public void should_fail_when_wrongPassword() throws Exception {
      member = memberTestHelper.builder().build();
      post = postTestHelper.builder()
          .member(member)
          .isSecret(true)
          .password("????????????")
          .build();

      em.flush();
      em.clear();
      bestMember = memberRepository.findById(bestMember.getId()).orElseThrow();
      post = postRepository.findById(post.getId()).orElseThrow();

      assertThrows(BusinessException.class, () -> {
        postService.find(bestMember, post.getId(), "??????");
      });

      assertThrows(BusinessException.class, () -> {
        postService.find(bestMember, post.getId(), null);
      });
    }
  }

  @Nested
  @DisplayName("????????? ??????")
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
          .title("????????? ??????")
          .content("????????? ??????")
          .member(member)
          .ipAddress(WebUtil.getUserIP())
          .build();
    }

    @Test
    @DisplayName("?????? ????????? ???????????? ?????? ????????? ????????? ????????????.")
    public void should_success_when_writerIsMe() throws Exception {
      long postId = postTestHelper.builder().member(member).build().getId();

      Post newPost = Post.builder()
          .title("?????? ??????")
          .content("?????? ??????")
          .build();

      assertDoesNotThrow(() -> {
        postService.update(member, postId, newPost, null, null);
      });
    }

    @Test
    @DisplayName("????????? ????????? ????????? ????????????.")
    public void should_success_when_updateThumbnail() throws Exception {
      long postId = postService.create(post, category.getId(), thumbnail, null);
      Thumbnail oldThumbnail = post.getThumbnail();

      Post newPost = Post.builder()
          .title("?????? ??????")
          .content("?????? ??????")
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
    @DisplayName("????????? ?????? ????????? ????????????.")
    public void should_success_when_updateFiles() throws Exception {
      long postId = postService.create(post, category.getId(), null, List.of(file1));
      List<FileEntity> beforeFiles = fileRepository.findAllByPost(post);

      Post newPost = Post.builder()
          .title("?????? ??????")
          .content("?????? ??????")
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
    @DisplayName("?????? ???????????? ????????? ?????? ??????????????? ????????? ????????? ????????? ????????????.")
    public void should_fail_when_secretPostWithoutPassword() throws Exception {
      long postId = postTestHelper.builder().member(member).build().getId();

      Post newPost = Post.builder()
          .title("?????? ??????")
          .content("?????? ??????")
          .isSecret(true)
          .build();

      assertThrows(BusinessException.class, () -> {
        postService.update(member, postId, newPost, null, null);
      });
    }
  }

  @Nested
  @DisplayName("????????? ????????? ?????????")
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
    @DisplayName("????????? ????????? ???????????? ????????????.")
    public void ?????????_?????????_????????????_????????????() throws Exception {
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
    @DisplayName("????????? ????????? ????????? ????????? ????????????.")
    public void ?????????_?????????_?????????_?????????_????????????() throws Exception {
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
