package com.keeper.homepage.domain.post.dao.category;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.post.entity.category.Category;
import com.keeper.homepage.domain.post.entity.category.Category.CategoryType;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CategoryRepositoryTest extends IntegrationTest {

  @Test
  @DisplayName("CategoryType Enum에 DB의 모든 데이터가 들어가 있어야 한다.")
  public void CategoryType_Enum에_DB의_모든_데이터가_들어가_있어야_한다() throws Exception {
    List<Category> categories = categoryRepository.findAll();
    List<Long> categoryIds = Arrays.stream(CategoryType.values())
        .map(CategoryType::getId)
        .toList();

    assertThat(getId(categories)).containsAll(categoryIds);
  }

  private static List<Long> getId(List<Category> categories) {
    return categories.stream()
        .map(Category::getId)
        .toList();
  }
}
