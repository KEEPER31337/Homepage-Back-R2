package com.keeper.homepage.domain.posting;

import com.keeper.homepage.domain.posting.dao.category.CategoryRepository;
import com.keeper.homepage.domain.posting.entity.category.Category;
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

    private Category parentCategory;

    private String href;

    private CategoryBuilder() {

    }

    public CategoryBuilder name(String name) {
      this.name = name;
      return this;
    }

    public CategoryBuilder parentCategory(Category parentCategory) {
      this.parentCategory = parentCategory;
      return this;
    }

    public CategoryBuilder href(String href) {
      this.href = href;
      return this;
    }

    public Category build() {
      return categoryRepository.save(Category.builder()
          .name(name != null ? name : "카테고리 이름")
          .parentCategory(parentCategory)
          .href(href)
          .build());
    }
  }

}
