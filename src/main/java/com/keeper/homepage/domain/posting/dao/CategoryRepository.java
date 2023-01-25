package com.keeper.homepage.domain.posting.dao;

import com.keeper.homepage.domain.posting.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
