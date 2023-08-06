package com.keeper.homepage.domain.post.application;

import static com.keeper.homepage.domain.post.application.PostService.EXAM_ACCESSIBLE_POINT;
import static com.keeper.homepage.domain.post.entity.category.Category.CategoryType.시험게시판;
import static com.keeper.homepage.domain.post.entity.category.Category.CategoryType.익명게시판;
import static com.keeper.homepage.domain.post.entity.category.Category.CategoryType.자유게시판;
import static com.keeper.homepage.domain.post.entity.category.Category.getCategoryBy;
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
import com.keeper.homepage.domain.post.dto.response.PostDetailResponse;
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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

public class PostServiceTest extends IntegrationTest {

  private Member member;
  private Category category;
  private MockMultipartFile thumbnail;
  private Post post;
  private long postId;

  @BeforeEach
  void setUp() {
    member = memberTestHelper.generate();
    category = getCategoryBy(자유게시판);
    thumbnail = thumbnailTestHelper.getThumbnailFile();
    post = postTestHelper.builder().member(member).category(category).build();
    postId = post.getId();
  }

  @Nested
  @DisplayName("게시글 생성")
  class CreatePost {

    @Test
    @DisplayName("게시글을 생성하면 성공적으로 저장된다.")
    void should_success_when_createPost() {
      postId = postService.create(post, category.getId(), thumbnail, List.of(thumbnail));
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
      assertThat(findPost.getCategory().getId()).isEqualTo(category.getId());
      assertThat(findPost.getThumbnail()).isNotNull();
      assertThat(findPost.getPostHasFiles()).isNotEmpty();
    }

    @Test
    @DisplayName("요청한 카테고리를 찾을 수 없는 경우 BusinessException 던진다.")
    void should_throwBusinessException_when_requestNotFoundCategory() {
      assertThatThrownBy(() -> postService.create(post, 0L, thumbnail, List.of(thumbnail)))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining("존재하지 않는 게시글 카테고리입니다.");
    }
  }

  @Nested
  @DisplayName("게시글 조회")
  class FindPost {

    private Member bestMember;
    private Category category, examCategory;
    private final long virtualPostId = 1;

    @BeforeEach
    void setUp() {
      bestMember = memberTestHelper.builder().point(EXAM_ACCESSIBLE_POINT).build();
      category = getCategoryBy(자유게시판);
      examCategory = getCategoryBy(시험게시판);
    }

    @Test
    @DisplayName("게시글을 조회하면 성공적으로 조회된다.")
    void should_success_when_getPost() {
      post = postTestHelper.builder()
          .member(bestMember)
          .password("비밀비밀")
          .category(category)
          .build();

      em.flush();
      em.clear();
      bestMember = memberRepository.findById(bestMember.getId()).orElseThrow();
      post = postRepository.findById(post.getId()).orElseThrow();
      PostDetailResponse response = postService.find(bestMember, post.getId(), "비밀비밀");

      assertThat(response.getTitle()).isEqualTo(post.getTitle());
      assertThat(response.getWriterName()).isEqualTo(bestMember.getProfile().getNickname().get());
      assertThat(response.getRegisterTime()).isEqualTo(post.getRegisterTime());
      assertThat(response.getVisitCount()).isEqualTo(post.getVisitCount());
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
      Category anonymousCategory = categoryRepository.findById(익명게시판.getId())
          .orElseThrow();
      post = postTestHelper.builder()
          .member(bestMember)
          .category(anonymousCategory)
          .build();

      em.flush();
      em.clear();
      bestMember = memberRepository.findById(bestMember.getId()).orElseThrow();
      post = postRepository.findById(post.getId()).orElseThrow();

      PostDetailResponse response = postService.find(bestMember, post.getId(), null);

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

    @Test
    @DisplayName("id 기준으로 카테고리에 해당하는 이전과 다음 게시글 하나는 성공적으로 조회된다.")
    public void id_기준으로_카테고리에_해당하는_이전과_다음_게시글_하나는_성공적으로_조회된다() throws Exception {
      Post first = postTestHelper.builder().category(PostServiceTest.this.category).build();
      Post middle = postTestHelper.builder().member(member).category(PostServiceTest.this.category).build();
      Post last = postTestHelper.builder().category(PostServiceTest.this.category).build();

      em.flush();
      em.clear();
      PostDetailResponse response = postService.find(member, middle.getId(), null);

      assertThat(response.getPreviousPost().getPostId()).isEqualTo(first.getId());
      assertThat(response.getNextPost().getPostId()).isEqualTo(last.getId());
    }

    @Test
    @DisplayName("이전, 다음 게시글으로 임시 저장글은 조회되면 안돤다.")
    public void 이전_다음_게시글으로_임시_저장글은_조회되면_안돤다() throws Exception {
      Post first = postTestHelper.builder().category(PostServiceTest.this.category).isTemp(true).build();
      Post middle = postTestHelper.builder().member(member).category(PostServiceTest.this.category).build();
      Post last = postTestHelper.builder().category(PostServiceTest.this.category).build();

      em.flush();
      em.clear();
      PostDetailResponse response = postService.find(member, middle.getId(), null);

      assertThat(response.getPreviousPost().getPostId()).isNotEqualTo(first.getId());
      assertThat(response.getNextPost().getPostId()).isEqualTo(last.getId());
    }

    @Test
    @DisplayName("이전, 혹은 다음 게시글이 없을 경우 null로 조회된다.")
    public void 이전_혹은_다음_게시글이_없을_경우_null로_조회된다() throws Exception {
      Post first = postTestHelper.builder().category(PostServiceTest.this.category).build();
      Post middle = postTestHelper.builder().member(member).category(PostServiceTest.this.category).build();

      em.flush();
      em.clear();
      PostDetailResponse response = postService.find(member, middle.getId(), null);

      assertThat(response.getPreviousPost().getPostId()).isEqualTo(first.getId());
      assertThat(response.getNextPost()).isEqualTo(null);
    }
  }

  @Nested
  @DisplayName("게시글 수정")
  class UpdatePost {

    private MockMultipartFile file1, file2;

    @BeforeEach
    void setUp() throws IOException {
      member = memberTestHelper.builder().point(EXAM_ACCESSIBLE_POINT).build();
      file1 = new MockMultipartFile("file", "testImage_1x1.png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_1x1.png"));
      file2 = new MockMultipartFile("file",
          "testImage_210x210.png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_210x210.png"));
    }

    @Test
    @DisplayName("내가 작성한 게시글인 경우 게시글 수정은 성공한다.")
    public void should_success_when_writerIsMe() throws Exception {
      postId = postTestHelper.builder().member(member).build().getId();

      Post newPost = Post.builder()
          .title("수정 제목")
          .content("수정 내용")
          .allowComment(true)
          .isNotice(false)
          .isSecret(false)
          .isTemp(false)
          .build();

      assertDoesNotThrow(() -> {
        postService.update(member, postId, newPost);
      });
    }

    @Test
    @DisplayName("게시글 썸네일 수정은 성공한다.")
    public void should_success_when_updateThumbnail() throws Exception {
      post = postTestHelper.builder().member(member).build();
      postId = postService.create(post, category.getId(), thumbnail, null);
      Thumbnail oldThumbnail = post.getThumbnail();

      MockMultipartFile newThumbnailFile = thumbnailTestHelper.getThumbnailFile();
      postService.updatePostThumbnail(member, postId, newThumbnailFile);
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
    @DisplayName("비밀 게시글로 설정한 경우 패스워드가 없으면 게시글 수정은 실패한다.")
    public void should_fail_when_secretPostWithoutPassword() throws Exception {
      postId = postTestHelper.builder().member(member).build().getId();

      Post newPost = Post.builder()
          .title("수정 제목")
          .content("수정 내용")
          .isSecret(true)
          .build();

      assertThrows(BusinessException.class, () -> {
        postService.update(member, postId, newPost);
      });
    }
  }

  @Nested
  @DisplayName("게시글 삭제")
  class DeletePost {

    @BeforeEach
    void setUp() throws IOException {
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

  @Nested
  @DisplayName("게시글 파일 삭제")
  class DeletePostFile {

    @Test
    @DisplayName("게시글 파일 삭제는 성공해야 한다.")
    public void 게시글_파일_삭제는_성공해야_한다() throws Exception {
      postService.addPostFiles(member, postId, List.of(thumbnail));

      FileEntity beforeFile = postHasFileRepository.findByPost(post)
          .orElseThrow()
          .getFile();

      assertThat(new File(beforeFile.getFilePath())).exists();
      assertThat(fileRepository.findById(beforeFile.getId())).isNotEmpty();

      postService.deletePostFile(member, postId, beforeFile.getId());

      assertThat(new File(beforeFile.getFilePath())).doesNotExist();
      assertThat(fileRepository.findById(beforeFile.getId())).isEmpty();
    }
  }

  @Nested
  @DisplayName("게시글 목록 조회")
  class FindPosts {

    Category category;

    @BeforeEach
    void setUp() {
      category = getCategoryBy(자유게시판);
    }

    @Test
    @DisplayName("공지글과 임시글은 조회되지 않아야 한다.")
    public void 공지글과_임시글은_조회되지_않아야_한다() throws Exception {
      Long postId1 = postTestHelper.builder().category(category).isTemp(true).build().getId();
      Long postId2 = postTestHelper.builder().category(category).isNotice(true).build().getId();

      Page<PostResponse> posts = postService.getPosts(category.getId(), null, null, PageRequest.of(0, 10));

      assertThat(posts.getContent().stream().map(PostResponse::getId).toList()).doesNotContain(postId1);
      assertThat(posts.getContent().stream().map(PostResponse::getId).toList()).doesNotContain(postId2);
    }

    @Test
    @DisplayName("제목 검색은 대소문자 구분 없이 조회되어야 한다.")
    public void 제목_검색은_대소문자_구분_없이_조회되어야_한다() throws Exception {
      postTestHelper.builder().category(category).title("ABCD").build();

      Page<PostResponse> posts = postService.getPosts(category.getId(), "title", "abc", PageRequest.of(0, 10));

      assertThat(posts.getContent().stream().map(PostResponse::getTitle).toList()).contains("ABCD");
    }

    @Test
    @DisplayName("내용 검색은 대소문자 구분 없이 조회되어야 한다.")
    public void 내용_검색은_대소문자_구분_없이_조회되어야_한다() throws Exception {
      Long postId = postTestHelper.builder().category(category).content("ABCD").build().getId();

      Page<PostResponse> posts = postService.getPosts(category.getId(), "content", "abc", PageRequest.of(0, 10));

      assertThat(posts.getContent().stream().map(PostResponse::getId).toList()).contains(postId);
    }

    @Test
    @DisplayName("제목 or 내용 검색은 대소문자 구분 없이 모두 조회되어야 한다.")
    public void 제목_or_내용_검색은_대소문자_구분_없이_모두_조회되어야_한다() throws Exception {
      Long postId1 = postTestHelper.builder().category(category).title("ABCD").build().getId();
      Long postId2 = postTestHelper.builder().category(category).content("BCD").build().getId();

      Page<PostResponse> posts = postService.getPosts(category.getId(), "title+content", "bc", PageRequest.of(0, 10));

      assertThat(posts.getContent().stream().map(PostResponse::getId).toList()).contains(postId1);
      assertThat(posts.getContent().stream().map(PostResponse::getId).toList()).contains(postId2);
    }
  }
}
