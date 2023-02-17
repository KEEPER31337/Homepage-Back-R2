package com.keeper.homepage.domain.library.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
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

  @Column(name = "borrow_date", nullable = false)
  private LocalDateTime borrowDate;

  @Column(name = "expire_date", nullable = false)
  private LocalDateTime expireDate;

  @Builder
  private BookBorrowInfo(Member member, Book book, LocalDateTime borrowDate,
      LocalDateTime expireDate) {
    this.member = member;
    this.book = book;
    this.borrowDate = borrowDate;
    this.expireDate = expireDate;
  }

  public void registerBook(Book book) {
    this.book = book;
  }
}
