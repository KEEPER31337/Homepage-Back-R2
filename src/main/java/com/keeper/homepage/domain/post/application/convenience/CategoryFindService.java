package com.keeper.homepage.domain.post.application.convenience;

import static com.keeper.homepage.global.error.ErrorCode.POST_CATEGORY_NOT_FOUND;

import com.keeper.homepage.domain.post.dao.category.CategoryRepository;
import com.keeper.homepage.domain.post.entity.category.Category;
import com.keeper.homepage.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryFindService {

  private static final long VIRTUAL_CATEGORY_ID = 1;
  private final CategoryRepository categoryRepository;

  public Category findById(long categoryId) {
    return categoryRepository.findByIdAndIdNot(categoryId, VIRTUAL_CATEGORY_ID)
        .orElseThrow(
            () -> new BusinessException(categoryId, "categoryId", POST_CATEGORY_NOT_FOUND));
  }
}
