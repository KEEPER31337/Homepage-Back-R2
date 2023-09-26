package com.keeper.homepage.domain.library.entity;

import static com.keeper.homepage.domain.library.entity.BookBorrowLog.LogType.대출반려;
import static com.keeper.homepage.domain.library.entity.BookBorrowLog.LogType.반납완료;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "book_borrow_log")
public class BookBorrowLog extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "borrow_date", nullable = false, updatable = false)
  private LocalDateTime borrowDate;

  @Column(name = "expire_date", nullable = false, updatable = false)
  private LocalDateTime expireDate;

  @Column(name = "return_date", updatable = false)
  private LocalDateTime returnDate;

  @Column(name = "reject_date", updatable = false)
  private LocalDateTime rejectDate;

  @Column(name = "book_id", nullable = false, updatable = false)
  private Long bookId;

  @Column(name = "book_title", nullable = false, updatable = false)
  private String bookTitle;

  @Column(name = "book_author", nullable = false, updatable = false)
  private String bookAuthor;

  @Column(name = "borrow_id", nullable = false, updatable = false)
  private Long borrowId;

  @Column(name = "status", nullable = false, updatable = false)
  private String borrowStatus;

  @Column(name = "member_id", nullable = false, updatable = false)
  private Long memberId;

  @Column(name = "member_realname", nullable = false, updatable = false)
  private String memberRealName;

  public static BookBorrowLog of(BookBorrowInfo bookBorrowInfo, LogType logType) {
    LocalDateTime now = LocalDateTime.now();
    return BookBorrowLog.builder()
        .borrowDate(bookBorrowInfo.getBorrowDate())
        .expireDate(bookBorrowInfo.getExpireDate())
        .returnDate(반납완료.equals(logType) ? now : null)
        .rejectDate(대출반려.equals(logType) ? now : null)
        .bookId(bookBorrowInfo.getBook().getId())
        .bookTitle(bookBorrowInfo.getBook().getTitle())
        .bookAuthor(bookBorrowInfo.getBook().getAuthor())
        .borrowId(bookBorrowInfo.getId())
        .borrowStatus(logType.name())
        .memberId(bookBorrowInfo.getMember().getId())
        .memberRealName(bookBorrowInfo.getMember().getRealName())
        .build();
  }

  @Builder
  private BookBorrowLog(LocalDateTime borrowDate, LocalDateTime expireDate,
      LocalDateTime returnDate, LocalDateTime rejectDate, Long bookId, String bookTitle,
      String bookAuthor, Long borrowId, String borrowStatus, Long memberId, String memberRealName) {
    this.borrowDate = borrowDate;
    this.expireDate = expireDate;
    this.returnDate = returnDate;
    this.rejectDate = rejectDate;
    this.bookId = bookId;
    this.bookTitle = bookTitle;
    this.bookAuthor = bookAuthor;
    this.borrowId = borrowId;
    this.borrowStatus = borrowStatus;
    this.memberId = memberId;
    this.memberRealName = memberRealName;
  }

  public Long getId() {
    return id;
  }

  public LocalDateTime getBorrowDate() {
    return borrowDate;
  }

  public LocalDateTime getExpireDate() {
    return expireDate;
  }

  public LocalDateTime getReturnDate() {
    return returnDate;
  }

  public LocalDateTime getRejectDate() {
    return rejectDate;
  }

  public Long getBookId() {
    return bookId;
  }

  public String getBookTitle() {
    return bookTitle;
  }

  public String getBookAuthor() {
    return bookAuthor;
  }

  public Long getBorrowId() {
    return borrowId;
  }

  public String getBorrowStatus() {
    return borrowStatus;
  }

  public Long getMemberId() {
    return memberId;
  }

  public String getMemberRealName() {
    return memberRealName;
  }

  public enum LogType {
    대출중, 반납대기, 반납완료, 대출반려, 전체,
  }
}
