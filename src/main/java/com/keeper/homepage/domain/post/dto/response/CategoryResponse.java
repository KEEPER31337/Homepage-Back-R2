package com.keeper.homepage.domain.post.dto.response;

import com.keeper.homepage.domain.post.entity.category.Category.CategoryType;

public record CategoryResponse(String categoryName) {

  public static CategoryResponse from(CategoryType categoryType) {
    return new CategoryResponse(categoryType.getName());
  }
}
