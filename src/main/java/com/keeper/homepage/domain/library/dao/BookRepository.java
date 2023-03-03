package com.keeper.homepage.domain.library.dao;

import com.keeper.homepage.domain.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

}
