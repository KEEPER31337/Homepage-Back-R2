package com.keeper.homepage.domain.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.dto.request.PostRequest;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.category.Category;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.web.WebUtil;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

public class PostServiceTest extends IntegrationTest {

  private Member member;
  private Category category;
  private MockMultipartFile thumbnail;

  @BeforeEach
  void setUp() {
    member = memberTestHelper.generate();
    category = categoryTestHelper.generate();
    thumbnail = thumbnailTestHelper.getThumbnailFile();
  }

  @Nested
  @DisplayName("게시글 생성")
  class CreatePost {

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

      Long postId = postService.createPost(post, category.getId(), thumbnail, List.of(thumbnail));
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

      assertThatThrownBy(() -> postService.createPost(post, 0L, thumbnail, List.of(thumbnail)))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining("존재하지 않는 카테고리입니다.");
    }
  }
}
