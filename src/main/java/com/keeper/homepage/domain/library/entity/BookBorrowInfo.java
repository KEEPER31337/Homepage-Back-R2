package com.keeper.homepage.domain.library.entity;

import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출대기;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.대출중;
import static com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType.반납대기;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.library.entity.BookBorrowStatus.BookBorrowStatusType;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = PROTECTED)
@Table(name = "book_borrow_info")
public class BookBorrowInfo extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "status_id", nullable = false)
  private BookBorrowStatus borrowStatus;

  @Column(name = "borrow_date", nullable = true)
  private LocalDateTime borrowDate;

  @Column(name = "expire_date", nullable = true)
  private LocalDateTime expireDate;

  @Column(name = "last_request_date", nullable = true)
  private LocalDateTime lastRequestDate;

  @Builder
  private BookBorrowInfo(Member member, Book book, BookBorrowStatus borrowStatus,
      LocalDateTime borrowDate,
      LocalDateTime expireDate,
      LocalDateTime lastRequestDate) {
    this.member = member;
    this.book = book;
    this.borrowStatus = borrowStatus;
    this.borrowDate = borrowDate;
    this.expireDate = expireDate;
    this.lastRequestDate = lastRequestDate;
  }

  public Long getId() {
    return id;
  }

  public Member getMember() {
    return member;
  }

  public Book getBook() {
    return book;
  }

  public BookBorrowStatus getBorrowStatus() {
    return borrowStatus;
  }

  public LocalDateTime getBorrowDate() {
    return borrowDate;
  }

  public LocalDateTime getExpireDate() {
    return expireDate;
  }

  public LocalDateTime getLastRequestDate() {
    return lastRequestDate;
  }

  public boolean isWaitOrInBorrowing() {
    BookBorrowStatusType type = getBorrowStatus().getType();
    return type.equals(대출대기) || isInBorrowing();
  }

  public boolean isInBorrowing() {
    BookBorrowStatusType type = getBorrowStatus().getType();
    return type.equals(대출중) || type.equals(반납대기);
  }

  public boolean isStatus(BookBorrowStatusType statusType) {
    BookBorrowStatusType type = getBorrowStatus().getType();
    return type.equals(statusType);
  }

  public void changeBorrowStatus(BookBorrowStatusType type) {
    this.borrowStatus = BookBorrowStatus.getBookBorrowStatusBy(type);
  }

  public void changeLastRequestDate(LocalDateTime lastRequestDate) {
    this.lastRequestDate = lastRequestDate;
  }

  public void setBorrowTime(LocalDateTime borrowTime) {
    this.borrowDate = borrowTime;
    this.expireDate = borrowTime.plusWeeks(2);
  }

  public boolean isMine(Member member) {
    return this.member.equals(member);
  }
}
