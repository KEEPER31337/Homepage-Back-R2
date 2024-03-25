package com.keeper.homepage.domain.post.dao;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.category.Category;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

  Optional<Post> findByIdAndIdNot(Long postId, Long virtualId);

  /**
   * 카테고리 + 공지글 제외 + 임시글 제외 + 등록시간 최신순 정렬
   *
   * @param category 게시글 카테고리
   * @param pageable Pageable
   */
  @Query("SELECT p FROM Post p "
      + "WHERE p.category = :category "
      + "AND p.isNotice = false "
      + "AND p.isTemp = false "
      + "AND p.id <> 1 " // virtual post
      + "ORDER BY p.registerTime DESC")
  Page<Post> findAllRecentByCategory(@Param("category") Category category, Pageable pageable);

  /**
   * 카테고리 + 공지글 제외 + 임시글 제외 + 제목 검색 + 등록시간 최신순 정렬
   *
   * @param category 게시글 카테고리
   * @param search   검색어
   * @param pageable Pageable
   */
  @Query("SELECT p FROM Post p "
      + "WHERE p.category = :category "
      + "AND p.isNotice = false "
      + "AND p.isTemp = false "
      + "AND LOWER(p.title) LIKE LOWER(concat('%', :search, '%')) "
      + "ORDER BY p.registerTime DESC")
  Page<Post> findAllRecentByCategoryAndTitle(@Param("category") Category category, @Param("search") String search,
      Pageable pageable);

  /**
   * 카테고리 + 공지글 제외 + 임시글 제외 + 내용 검색 + 등록시간 최신순 정렬
   *
   * @param category 게시글 카테고리
   * @param search   검색어
   * @param pageable Pageable
   */
  @Query("SELECT p FROM Post p "
      + "WHERE p.category = :category "
      + "AND p.isNotice = false "
      + "AND p.isTemp = false "
      + "AND LOWER(p.content) LIKE LOWER(concat('%', :search, '%')) "
      + "ORDER BY p.registerTime DESC")
  Page<Post> findAllRecentByCategoryAndContent(@Param("category") Category category, @Param("search") String search,
      Pageable pageable);

  /**
   * 카테고리 + 공지글 제외 + 임시글 제외 + 제목&내용 검색 + 등록시간 최신순 정렬
   *
   * @param category 게시글 카테고리
   * @param search   검색어
   * @param pageable Pageable
   */
  @Query("SELECT p FROM Post p "
      + "WHERE p.category = :category "
      + "AND p.isNotice = false "
      + "AND p.isTemp = false "
      + "AND (LOWER(p.title) LIKE LOWER(concat('%', :search, '%')) "
      + "OR LOWER(p.content) LIKE LOWER(concat('%', :search, '%'))) "
      + "ORDER BY p.registerTime DESC")
  Page<Post> findAllRecentByCategoryAndTitleOrContent(@Param("category") Category category,
      @Param("search") String search, Pageable pageable);

  /**
   * 카테고리 + 공지글 제외 + 임시글 제외 + 작성자 실명 검색 + 등록시간 최신순 정렬
   *
   * @param category 게시글 카테고리
   * @param search   검색어
   * @param pageable Pageable
   */
  @Query("SELECT p FROM Post p "
      + "WHERE p.category = :category "
      + "AND p.isNotice = false "
      + "AND p.isTemp = false "
      + "AND LOWER(p.member.profile.realName) LIKE LOWER(concat('%', :search, '%')) "
      + "ORDER BY p.registerTime DESC")
  Page<Post> findAllRecentByCategoryAndWriter(@Param("category") Category category, @Param("search") String search,
      Pageable pageable);

  Page<Post> findAllByMemberAndIsTempFalse(Member member, Pageable pageable);

  Page<Post> findAllByMemberAndIsTempTrue(Member member, Pageable pageable);

  void deleteAllByMemberAndIsTempTrue(Member member);
}
