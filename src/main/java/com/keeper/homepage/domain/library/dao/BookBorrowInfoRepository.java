package com.keeper.homepage.domain.library.dao;

import com.keeper.homepage.domain.library.entity.BookBorrowInfo;
import com.keeper.homepage.domain.library.entity.BookBorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookBorrowInfoRepository extends JpaRepository<BookBorrowInfo, Long> {

  Page<BookBorrowInfo> findAllByBorrowStatus(BookBorrowStatus status, Pageable pageable);
}
