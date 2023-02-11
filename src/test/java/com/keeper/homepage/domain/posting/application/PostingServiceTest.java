package com.keeper.homepage.domain.posting.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.posting.dto.request.PostRequest;
import com.keeper.homepage.domain.posting.entity.Posting;
import com.keeper.homepage.domain.posting.entity.category.Category;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.web.WebUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class PostingServiceTest extends IntegrationTest {

  private Member member;
  private Category category;

  @BeforeEach
  void setUp() {
    member = memberTestHelper.generate();
    category = categoryTestHelper.generate();
  }

  @Nested
  @DisplayName("게시글 생성")
  class CreatePost {

    private final MockMultipartFile validMultipartFile = new MockMultipartFile("image",
        "testImage_210x210.png", "image/png",
        new FileInputStream("src/test/resources/images/testImage_210x210.png"));

    CreatePost() throws IOException {
    }

    @Test
    @DisplayName("게시글을 생성하면 성공적으로 저장된다.")
    void should_success_when_createPost() {
      PostRequest request = PostRequest.builder()
          .title("게시글 제목")
          .content("게시글 내용")
          .allowComment(true)
          .isNotice(true)
          .isSecret(true)
          .isTemp(true)
          .password("게시글 암호")
          .categoryId(category.getId())
          .build();

      Long postId = postingService.createPost(member, request);
      em.flush();
      em.clear();
      Posting findPosting = postingRepository.findById(postId).orElseThrow();

      assertThat(findPosting.getTitle()).isEqualTo("게시글 제목");
      assertThat(findPosting.getContent()).isEqualTo("게시글 내용");
      assertThat(findPosting.getMember()).isEqualTo(member);
      assertThat(findPosting.getVisitCount()).isEqualTo(0);
      assertThat(findPosting.getLikeCount()).isEqualTo(0);
      assertThat(findPosting.getDislikeCount()).isEqualTo(0);
      assertThat(findPosting.getCommentCount()).isEqualTo(0);
      assertThat(findPosting.getIpAddress()).isEqualTo(WebUtil.getUserIP());
      assertThat(findPosting.getAllowComment()).isEqualTo(true);
      assertThat(findPosting.getIsNotice()).isEqualTo(true);
      assertThat(findPosting.getIsSecret()).isEqualTo(true);
      assertThat(findPosting.getIsTemp()).isEqualTo(true);
      assertThat(findPosting.getPassword()).isEqualTo("게시글 암호");
      assertThat(findPosting.getCategory().getId()).isEqualTo(category.getId());
      assertThat(findPosting.getThumbnail()).isNotNull();
      assertThat(findPosting.getFiles()).isNotEmpty();
    }

    @Test
    @DisplayName("null일 수 있는 request의 null은 허용한다.")
    void should_allowNull_when_nullableRequest() {
      PostRequest request = PostRequest.builder()
          .title("게시글 제목")
          .content("게시글 내용")
          .categoryId(category.getId())
          .build();

      Long postId = postingService.createPost(member, request);
      em.flush();
      em.clear();
      Posting findPosting = postingRepository.findById(postId).orElseThrow();

      assertThat(findPosting.getTitle()).isEqualTo("게시글 제목");
      assertThat(findPosting.getContent()).isEqualTo("게시글 내용");
      assertThat(findPosting.getMember()).isEqualTo(member);
      assertThat(findPosting.getVisitCount()).isEqualTo(0);
      assertThat(findPosting.getLikeCount()).isEqualTo(0);
      assertThat(findPosting.getDislikeCount()).isEqualTo(0);
      assertThat(findPosting.getCommentCount()).isEqualTo(0);
      assertThat(findPosting.getIpAddress()).isEqualTo(WebUtil.getUserIP());
      assertThat(findPosting.getAllowComment()).isEqualTo(true);
      assertThat(findPosting.getIsNotice()).isEqualTo(false);
      assertThat(findPosting.getIsSecret()).isEqualTo(false);
      assertThat(findPosting.getIsTemp()).isEqualTo(false);
      assertThat(findPosting.getPassword()).isNull();
      assertThat(findPosting.getCategory().getId()).isEqualTo(category.getId());
      assertThat(findPosting.getThumbnail()).isNull();
      assertThat(findPosting.getFiles()).isEmpty();
    }

    @Test
    @DisplayName("요청한 카테고리를 찾을 수 없는 경우 BusinessException 던진다.")
    void should_throwBusinessException_when_requestNotFoundCategory() {
      PostRequest request = PostRequest.builder()
          .title("게시글 제목")
          .content("게시글 내용")
          .categoryId(999L)
          .build();

      assertThatThrownBy(() -> postingService.createPost(member, request))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining("존재하지 않는 카테고리입니다.");
    }
  }
}
