package com.keeper.homepage.domain.library.dao;

import com.keeper.homepage.domain.library.entity.Book;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long> {

  Page<Book> findAllByTitleIgnoreCaseContaining(String search, Pageable pageable);

  Page<Book> findAllByAuthorIgnoreCaseContaining(String search, Pageable pageable);

  @Query("SELECT b FROM Book b "
      + "WHERE LOWER(b.title) LIKE LOWER(concat('%', :search, '%')) "
      + "OR LOWER(b.author) LIKE LOWER(concat('%', :search, '%'))")
  Page<Book> findAllByTitleOrAuthor(@Param("search") String search, Pageable pageable);
}
