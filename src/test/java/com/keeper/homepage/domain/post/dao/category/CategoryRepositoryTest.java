package com.keeper.homepage.domain.post.dao.category;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.post.entity.category.Category;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CategoryRepositoryTest extends IntegrationTest {

  private Category parent;
  private Category child;

  @BeforeEach
  void setUp() {
    parent = categoryTestHelper.generate();
    child = categoryTestHelper.generate();
  }

  @Nested
  @DisplayName("Category Save 테스트")
  class CategorySaveTest {

    @Test
    @DisplayName("부모 카테고리에 자식 카테고리를 등록하면 DB에 저장되어야 한다.")
    void should_saveChildCategory_when_parentAddChild() {
      parent.addChild(child);

      em.flush();
      em.clear();
      parent = categoryRepository.findById(parent.getId()).orElseThrow();
      child = categoryRepository.findById(child.getId()).orElseThrow();
      List<Category> categories = categoryRepository.findAll();

      assertThat(parent.getChildren()).hasSize(1);
      assertThat(parent.getChildren()).contains(child);
      assertThat(categories).contains(child);
    }
  }

  @Nested
  @DisplayName("Category Remove 테스트")
  class CategoryRemoveTest {

    @Test
    @DisplayName("부모 카테고리를 지우면 자식 카테고리들도 지워진다.")
    void should_deletedChildren_when_deleteParent() {
      parent.addChild(child);

      categoryRepository.delete(parent);

      em.flush();
      em.clear();
      List<Category> categories = categoryRepository.findAll();

      assertThat(categories).doesNotContain(child);
    }
  }
}
