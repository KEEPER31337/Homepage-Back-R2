package com.keeper.homepage.domain.post.dao.category;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.entity.category.Category;
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
  @DisplayName("Category Save 테스트")
  class CategorySaveTest {

    @Test
    @DisplayName("부모 카테고리에 자식 카테고리를 등록하면 DB에 저장되어야 한다.")
    void should_saveChildCategory_when_parentAddChild() {
      // TODO: [DB] self referencing 하는 foreign key로 설정되면 변경한다.
    }
  }

  @Nested
  @DisplayName("Category Remove 테스트")
  class CategoryRemoveTest {

    @Test
    @DisplayName("부모 카테고리를 지우면 자식 카테고리들도 지워진다.")
    void should_deletedChildren_when_deleteParent() {
      // TODO: [DB] self referencing 하는 foreign key로 설정되면 변경한다.
    }
  }

  @Nested
  @DisplayName("DB NOT NULL DEFAULT 테스트")
  class NotNullDefaultTest {

    @Test
    @DisplayName("부모 카테고리를 넣지 않았을 때 0L으로 처리해야 한다.")
      // TODO: [DB] 부모 카테고리가 없을 경우 null로 처리하도록 변경되면 수정한다.
    void should_processDefault_when_EmptyParentCategory() {
      em.flush();
      em.clear();

      Category findCategory = categoryRepository.findById(category.getId()).orElseThrow();

      assertThat(findCategory.getParent().getId()).isEqualTo(0L);
    }
  }
}
