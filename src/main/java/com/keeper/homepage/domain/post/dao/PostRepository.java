package com.keeper.homepage.domain.post.dao;

import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.category.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

  Optional<Post> findByIdAndIdNot(Long postId, Long virtualId);

  List<Post> findAllByCategoryAndIsNoticeTrue(Category category);

}
