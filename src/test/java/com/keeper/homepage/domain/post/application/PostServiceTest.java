package com.keeper.homepage.domain.post.application;

import static com.keeper.homepage.domain.post.application.PostService.EXAM_ACCESSIBLE_ATTENDANCE_COUNT;
import static com.keeper.homepage.domain.post.application.PostService.EXAM_ACCESSIBLE_COMMENT_COUNT;
import static com.keeper.homepage.domain.post.application.PostService.EXAM_ACCESSIBLE_POINT;
import static com.keeper.homepage.domain.post.entity.category.Category.DefaultCategory.ANONYMOUS_CATEGORY;
import static com.keeper.homepage.domain.post.entity.category.Category.DefaultCategory.EXAM_CATEGORY;
import static com.keeper.homepage.domain.post.entity.category.Category.DefaultCategory.VIRTUAL_CATEGORY;
import static com.keeper.homepage.domain.thumbnail.entity.Thumbnail.DefaultThumbnail.POST_THUMBNAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.Nickname;
import com.keeper.homepage.domain.post.dto.response.PostResponse;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.category.Category;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil;
import com.keeper.homepage.global.util.web.WebUtil;
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
      assertThat(findPost.getLikeCount()).isEqualTo(0);
      assertThat(findPost.getDislikeCount()).isEqualTo(0);
      assertThat(findPost.getCommentCount()).isEqualTo(0);
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
      thumbnail = thumbnailRepository.findById(POST_THUMBNAIL.getId()).orElseThrow();
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
          .isEqualTo(thumbnailUtil.getThumbnailPath(POST_THUMBNAIL.getPath()));
      assertThat(response.getContent()).isEqualTo(post.getContent());
      assertThat(response.getLikeCount()).isEqualTo(post.getLikeCount());
      assertThat(response.getDislikeCount()).isEqualTo(post.getDislikeCount());
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
    @DisplayName("Virtual Post 데이터는 조회할 수 없다.")
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
}
