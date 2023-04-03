package com.keeper.homepage.domain.library;

import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출대기중;

import com.keeper.homepage.domain.library.dao.BookBorrowInfoRepository;
import com.keeper.homepage.domain.library.dao.BookBorrowStatusRepository;
import com.keeper.homepage.domain.library.entity.Book;
import com.keeper.homepage.domain.library.entity.BookBorrowInfo;
import com.keeper.homepage.domain.library.entity.BookBorrowStatus;
import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookBorrowInfoTestHelper {

  @Autowired
  BookBorrowInfoRepository bookBorrowInfoRepository;
  @Autowired
  BookTestHelper bookTestHelper;
  @Autowired
  MemberTestHelper memberTestHelper;
  @Autowired
  BookBorrowStatusRepository bookBorrowStatusRepository;

  public BookBorrowInfo generate() {
    return this.builder().build();
  }

  public BookBorrowInfoBuilder builder() {
    return new BookBorrowInfoBuilder();
  }

  public final class BookBorrowInfoBuilder {

    private Member member;
    private Book book;
    private BookBorrowStatus borrowStatus;
    private LocalDateTime borrowDate;
    private LocalDateTime expireDate;

    private BookBorrowInfoBuilder() {
    }

    public BookBorrowInfoBuilder member(Member member) {
      this.member = member;
      return this;
    }

    public BookBorrowInfoBuilder book(Book book) {
      this.book = book;
      return this;
    }

    public BookBorrowInfoBuilder borrowStatus(BookBorrowStatus borrowStatus) {
      this.borrowStatus = borrowStatus;
      return this;
    }

    public BookBorrowInfoBuilder borrowDate(LocalDateTime borrowDate) {
      this.borrowDate = borrowDate;
      return this;
    }

    public BookBorrowInfoBuilder expireDate(LocalDateTime expireDate) {
      this.expireDate = expireDate;
      return this;
    }

    public BookBorrowInfo build() {
      LocalDateTime now = LocalDateTime.now();
      BookBorrowInfo borrow = bookBorrowInfoRepository.save(BookBorrowInfo.builder()
          .member(member != null ? member : memberTestHelper.generate())
          .book(book != null ? book : bookTestHelper.generate())
          .borrowDate(borrowDate != null ? borrowDate : now)
          .borrowStatus(borrowStatus != null ? borrowStatus : getDefaultBorrowStatus())
          .expireDate(expireDate != null ? expireDate : now.plusWeeks(2))
          .build());
      borrow.getBook().getBookBorrowInfos().add(borrow);
      return borrow;
    }

    private BookBorrowStatus getDefaultBorrowStatus() {
      return bookBorrowStatusRepository.findById(대출대기중.getId()).orElseThrow();
    }

  }
}
