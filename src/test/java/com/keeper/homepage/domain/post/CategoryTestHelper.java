package com.keeper.homepage.domain.post;

import com.keeper.homepage.domain.post.dao.category.CategoryRepository;
import com.keeper.homepage.domain.post.entity.category.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryTestHelper {

  @Autowired
  CategoryRepository categoryRepository;

  public Category generate() {
    return this.builder().build();
  }

  public CategoryBuilder builder() {
    return new CategoryBuilder();
  }

  public final class CategoryBuilder {

    private String name;

    private CategoryBuilder() {

    }

    public CategoryBuilder name(String name) {
      this.name = name;
      return this;
    }

    public Category build() {
      return categoryRepository.save(Category.builder()
          .name(name != null ? name : "카테고리 이름")
          .build());
    }
  }
}
