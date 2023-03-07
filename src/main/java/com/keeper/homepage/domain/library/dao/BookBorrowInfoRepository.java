package com.keeper.homepage.domain.library.dao;

import com.keeper.homepage.domain.library.entity.BookBorrowInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookBorrowInfoRepository extends JpaRepository<BookBorrowInfo, Long> {

}
