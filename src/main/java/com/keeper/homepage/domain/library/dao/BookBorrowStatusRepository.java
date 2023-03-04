package com.keeper.homepage.domain.library.dao;

import com.keeper.homepage.domain.library.entity.BookBorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookBorrowStatusRepository extends JpaRepository<BookBorrowStatus, Long> {

}
