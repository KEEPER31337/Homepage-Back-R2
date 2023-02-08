package com.keeper.homepage.domain.posting.dao.category;

import com.keeper.homepage.domain.posting.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
