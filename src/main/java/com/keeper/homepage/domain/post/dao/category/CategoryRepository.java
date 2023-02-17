package com.keeper.homepage.domain.post.dao.category;

import com.keeper.homepage.domain.post.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
