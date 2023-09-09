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

  @Query(value = "select borrow "
      + "from BookBorrowInfo borrow join borrow.book book join borrow.member member "
      + "where borrow.borrowStatus.id = :statusId "
      + "and (book.title like %:search% " // 도서명
      + "or book.author like %:search% " // 저자
      + "or member.profile.realName.realName like %:search%) " // 이름
  )
  Page<BookBorrowInfo> findAllByBorrowStatus(@Param("statusId") long statusIdId,
      @Param("search") String search, Pageable pageable);

  @Query(value = "select borrow "
      + "from BookBorrowInfo borrow join borrow.book book join borrow.member member "
      + "where (borrow.borrowStatus.id = :statusId1 "
      + "or borrow.borrowStatus.id = :statusId2) "
      + "and (book.title like %:search% " // 도서명
      + "or book.author like %:search% " // 저자
      + "or member.profile.realName.realName like %:search%) " // 이름
  )
  Page<BookBorrowInfo> findAllByTwoBorrowStatus(@Param("statusId1") long borrowStatusId,
      @Param("statusId2") long borrowStatus2Id, @Param("search") String search, Pageable pageable);

  @Query(value = "select borrow "
      + "from BookBorrowInfo borrow "
      + "where (borrow.borrowStatus.id = 3 " // 대출 중
      + "or borrow.borrowStatus.id = 4) " // 반납 대기
      + "and borrow.expireDate <= current_timestamp")
  Page<BookBorrowInfo> findAllOverDue(@Param("now") LocalDateTime now, Pageable pageable);

  Optional<BookBorrowInfo> findByMemberAndBook(Member member, Book book);

  Optional<BookBorrowInfo> findByMemberAndBookAndBorrowStatus(Member member, Book book, BookBorrowStatus borrowStatus);

  @Query(value = "SELECT borrow "
      + "FROM BookBorrowInfo borrow "
      + "WHERE (borrow.borrowStatus.id = 1 " // 대출 대기
      + "OR borrow.borrowStatus.id = 3 " // 대출 중
      + "OR borrow.borrowStatus.id = 4) " // 반납 대기
      + "AND borrow.member = :member ")
  Page<BookBorrowInfo> findAllByMemberAndInBorrowingOrWait(@Param("member") Member member, Pageable pageable);

}
