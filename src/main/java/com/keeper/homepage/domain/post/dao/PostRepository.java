package com.keeper.homepage.domain.post.dao;

import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.category.Category;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

  Optional<Post> findByIdAndIdNot(Long postId, Long virtualId);

  @Query("SELECT p FROM Post p "
      + "WHERE p.category = :category "
      + "AND p.isNotice = true "
      + "ORDER BY p.registerTime DESC")
  List<Post> findAllNoticeByCategory(@Param("category") Category category);

  @Query("SELECT p FROM Post p "
      + "WHERE p.isTemp = false "
      + "ORDER BY p.registerTime DESC")
  List<Post> findAllRecent();

  @Query("SELECT p FROM Post p "
      + "WHERE p.category = :category "
      + "AND p.isNotice = false "
      + "AND p.isTemp = false "
      + "ORDER BY p.registerTime DESC")
  Page<Post> findAllRecentByCategory(@Param("category") Category category, Pageable pageable);

  @Query("SELECT p FROM Post p "
      + "WHERE p.category = :category "
      + "AND p.isNotice = false "
      + "AND p.isTemp = false "
      + "AND LOWER(p.title) LIKE LOWER(concat('%', :search, '%')) "
      + "ORDER BY p.registerTime DESC")
  Page<Post> findAllRecentByCategoryAndTitle(@Param("category") Category category, @Param("search") String search,
      Pageable pageable);

  @Query("SELECT p FROM Post p "
      + "WHERE p.category = :category "
      + "AND p.isNotice = false "
      + "AND p.isTemp = false "
      + "AND LOWER(p.content) LIKE LOWER(concat('%', :search, '%')) "
      + "ORDER BY p.registerTime DESC")
  Page<Post> findAllRecentByCategoryAndContent(@Param("category") Category category, @Param("search") String search,
      Pageable pageable);

  @Query("SELECT p FROM Post p "
      + "WHERE p.category = :category "
      + "AND p.isNotice = false "
      + "AND p.isTemp = false "
      + "AND LOWER(p.title) LIKE LOWER(concat('%', :search, '%')) "
      + "OR LOWER(p.content) LIKE LOWER(concat('%', :search, '%'))"
      + "ORDER BY p.registerTime DESC")
  Page<Post> findAllRecentByCategoryAndTitleOrContent(@Param("category") Category category,
      @Param("search") String search, Pageable pageable);

  @Query("SELECT p FROM Post p "
      + "WHERE p.category = :category "
      + "AND p.isNotice = false "
      + "AND p.isTemp = false "
      + "AND LOWER(p.member.profile.nickname) LIKE LOWER(concat('%', :search, '%')) "
      + "ORDER BY p.registerTime DESC")
  Page<Post> findAllRecentByCategoryAndWriter(@Param("category") Category category, @Param("search") String search,
      Pageable pageable);

  @Query("SELECT p FROM Post p " +
      "WHERE p.isTemp = false " +
      "AND p.registerTime BETWEEN :startDate AND :endDate")
  List<Post> findAllTrend(@Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

}
