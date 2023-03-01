package com.keeper.homepage.domain.library.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.entity.BaseEntity;
import jakarta.persistence.*;

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

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "status_id", nullable = false)
  private BookBorrowStatus borrowStatus;

  @Column(name = "borrow_date", nullable = false)
  private LocalDateTime borrowDate;

  @Column(name = "expire_date", nullable = false)
  private LocalDateTime expireDate;

  @Builder
  private BookBorrowInfo(Member member, Book book, BookBorrowStatus borrowStatus, LocalDateTime borrowDate,
      LocalDateTime expireDate) {
    this.member = member;
    this.book = book;
    this.borrowStatus = borrowStatus;
    this.borrowDate = borrowDate;
    this.expireDate = expireDate;
  }

}
