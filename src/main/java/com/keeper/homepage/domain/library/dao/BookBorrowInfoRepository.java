package com.keeper.homepage.domain.library.dao;

import com.keeper.homepage.domain.library.entity.Book;
import com.keeper.homepage.domain.library.entity.BookBorrowInfo;
import com.keeper.homepage.domain.library.entity.BookBorrowStatus;
import com.keeper.homepage.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookBorrowInfoRepository extends JpaRepository<BookBorrowInfo, Long> {

  Page<BookBorrowInfo> findAllByBorrowStatus(BookBorrowStatus status, Pageable pageable);

  @Query(value = "select borrow "
      + "from BookBorrowInfo borrow "
      + "where (borrow.borrowStatus.id = 3 " // 대출승인
      + "or borrow.borrowStatus.id = 4) " // 반납대기중
      + "and borrow.expireDate <= current_timestamp")
  Page<BookBorrowInfo> findAllOverDue(@Param("now") LocalDateTime now, Pageable pageable);

  Optional<BookBorrowInfo> findByMemberAndBook(Member member, Book book);

  Optional<BookBorrowInfo> findByMemberAndBookAndBorrowStatus(Member member, Book book, BookBorrowStatus borrowStatus);
}
