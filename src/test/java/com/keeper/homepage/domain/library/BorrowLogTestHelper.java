package com.keeper.homepage.domain.library;

import com.keeper.homepage.domain.library.dao.BookBorrowInfoRepository;
import com.keeper.homepage.domain.library.dao.BookBorrowLogRepository;
import com.keeper.homepage.domain.library.dao.BookBorrowStatusRepository;
import com.keeper.homepage.domain.library.entity.BookBorrowLog;
import com.keeper.homepage.domain.library.entity.BookBorrowLog.LogType;
import com.keeper.homepage.domain.member.MemberTestHelper;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BorrowLogTestHelper {

  @Autowired
  BookBorrowLogRepository borrowLogRepository;
  @Autowired
  BookBorrowInfoRepository bookBorrowInfoRepository;
  @Autowired
  BookTestHelper bookTestHelper;
  @Autowired
  MemberTestHelper memberTestHelper;
  @Autowired
  BookBorrowStatusRepository bookBorrowStatusRepository;

  public BookBorrowLog generate() {
    return this.builder().build();
  }

  public BorrowLogBuilder builder() {
    return new BorrowLogBuilder();
  }

  public final class BorrowLogBuilder {

    private LocalDateTime borrowDate;
    private LocalDateTime expireDate;
    private LocalDateTime returnDate;
    private LocalDateTime rejectDate;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private Long borrowId;
    private String borrowStatus;
    private Long memberId;
    private String memberRealName;

    private BorrowLogBuilder() {
    }

    public BorrowLogBuilder borrowDate(LocalDateTime borrowDate) {
      this.borrowDate = borrowDate;
      return this;
    }

    public BorrowLogBuilder expireDate(LocalDateTime expireDate) {
      this.expireDate = expireDate;
      return this;
    }

    public BorrowLogBuilder returnDate(LocalDateTime returnDate) {
      this.returnDate = returnDate;
      return this;
    }

    public BorrowLogBuilder rejectDate(LocalDateTime rejectDate) {
      this.rejectDate = rejectDate;
      return this;
    }

    public BorrowLogBuilder bookId(Long bookId) {
      this.bookId = bookId;
      return this;
    }

    public BorrowLogBuilder bookTitle(String bookTitle) {
      this.bookTitle = bookTitle;
      return this;
    }

    public BorrowLogBuilder bookAuthor(String bookAuthor) {
      this.bookAuthor = bookAuthor;
      return this;
    }

    public BorrowLogBuilder borrowId(Long borrowId) {
      this.borrowId = borrowId;
      return this;
    }

    public BorrowLogBuilder borrowStatus(String borrowStatus) {
      this.borrowStatus = borrowStatus;
      return this;
    }

    public BorrowLogBuilder memberId(Long memberId) {
      this.memberId = memberId;
      return this;
    }

    public BorrowLogBuilder memberRealName(String memberRealName) {
      this.memberRealName = memberRealName;
      return this;
    }

    public BookBorrowLog build() {
      LocalDateTime now = LocalDateTime.now();
      BookBorrowLog borrowLog = borrowLogRepository.save(BookBorrowLog.builder()
          .borrowDate(borrowDate != null ? borrowDate : now)
          .expireDate(expireDate != null ? expireDate : now.plusWeeks(2))
          .returnDate(returnDate != null ? returnDate : null)
          .rejectDate(rejectDate != null ? rejectDate : null)
          .bookId(bookId != null ? bookId : 1)
          .bookTitle(bookTitle != null ? bookTitle : "책 제목")
          .bookAuthor(bookAuthor != null ? bookAuthor : "책 저자")
          .borrowId(borrowId != null ? borrowId : 1)
          .borrowStatus(borrowStatus != null ? borrowStatus : LogType.대출중.name())
          .memberId(memberId != null ? memberId : 1)
          .memberRealName(memberRealName != null ? memberRealName : "실명")
          .build());
      return borrowLog;
    }
  }
}
