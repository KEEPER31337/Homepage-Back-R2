package com.keeper.homepage.domain.posting.dao.category;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.posting.entity.Posting;
import com.keeper.homepage.domain.posting.entity.category.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CategoryRepositoryTest extends IntegrationTest {

  private Category category;
  private Member member;

  @BeforeEach
  void setUp() {
    category = categoryTestHelper.generate();
    member = memberTestHelper.generate();
  }

  @Nested
  @DisplayName("Category Remove 테스트")
  class CategoryRemoveTest {

    @Test
    @DisplayName("카테고리를 지우면 카테고리의 포스팅 글들도 지워진다.")
    void should_deletedPostings_when_deleteCategory() {
      Posting posting = Posting.builder()
          .title("포스팅 타이틀")
          .content("포스팅 내용")
          .member(member)
          .ipAddress("0.0.0.0")
          .build();
      category.addPosting(posting);

      categoryRepository.delete(category);

      assertThat(postingRepository.findAll()).doesNotContain(posting);
    }

    @Test
    @DisplayName("부모 카테고리를 지우면 자식 카테고리들도 지워진다.")
    void should_deletedChildren_when_deleteParent() {
      // TODO: DB가 변경되면 성공해야 합니다.
    }
  }

  @Nested
  @DisplayName("DB NOT NULL DEFAULT 테스트")
  class NotNullDefaultTest {

    @Test
    @DisplayName("부모 카테고리를 넣지 않았을 때 0L으로 처리해야 한다.")
    void should_process_when_EmptyParentId() {
      em.flush();
      em.clear();

      Category findCategory = categoryRepository.findById(category.getId()).orElseThrow();

      assertThat(findCategory.getParent().getId()).isEqualTo(0L);
    }
  }
}
