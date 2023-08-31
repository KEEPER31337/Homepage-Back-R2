package com.keeper.homepage.domain.library.dao;

import com.keeper.homepage.domain.library.entity.BookBorrowLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookBorrowLogRepository extends JpaRepository<BookBorrowLog, Long> {

}
