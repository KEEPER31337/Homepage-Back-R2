package com.keeper.homepage.domain.library.dao;

import com.keeper.homepage.domain.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

  Page<Book> findAllByTitleIgnoreCaseContainingOrderByIdDesc(String search, Pageable pageable);

  Page<Book> findAllByAuthorIgnoreCaseContainingOrderByIdDesc(String search, Pageable pageable);

  @Query("SELECT b FROM Book b "
      + "WHERE LOWER(b.title) LIKE LOWER(concat('%', :search, '%')) "
      + "OR LOWER(b.author) LIKE LOWER(concat('%', :search, '%')) "
      + "ORDER BY b.id DESC")
  Page<Book> findAllByTitleOrAuthor(@Param("search") String search, Pageable pageable);
}
