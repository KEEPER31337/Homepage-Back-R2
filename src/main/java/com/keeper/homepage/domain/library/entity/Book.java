package com.keeper.homepage.domain.library.entity;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.entity.BaseEntity;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.error.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "book")
public class Book extends BaseEntity {

  private static final int MAX_TITLE_LENGTH = 250;
  private static final int MAX_AUTHOR_LENGTH = 40;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "title", nullable = false, length = MAX_TITLE_LENGTH)
  private String title;

  @Column(name = "author", nullable = false, length = MAX_AUTHOR_LENGTH)
  private String author;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "book_department_id", nullable = false)
  private BookDepartment bookDepartment;

  @Column(name = "total_quantity", nullable = false)
  private Long totalQuantity;

  @Column(name = "current_quantity", nullable = false)
  private Long currentQuantity;

  @OneToOne(fetch = LAZY, cascade = REMOVE)
  @JoinColumn(name = "thumbnail_id")
  private Thumbnail thumbnail;

  @OneToMany(mappedBy = "book", cascade = REMOVE)
  private final List<BookBorrowInfo> bookBorrowInfos = new ArrayList<>();

  @Builder
  public Book(String title, String author, BookDepartment bookDepartment,
      Long totalQuantity, Long currentQuantity, Thumbnail thumbnail) {
    this.title = title;
    this.author = author;
    this.bookDepartment = bookDepartment;
    this.totalQuantity = totalQuantity;
    this.currentQuantity = currentQuantity;
    this.thumbnail = thumbnail;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getAuthor() {
    return author;
  }

  public BookDepartment getBookDepartment() {
    return bookDepartment;
  }

  public Long getTotalQuantity() {
    return totalQuantity;
  }

  public Long getCurrentQuantity() {
    return currentQuantity;
  }

  public Thumbnail getThumbnail() {
    return thumbnail;
  }

  public List<BookBorrowInfo> getBookBorrowInfos() {
    return bookBorrowInfos;
  }

  public boolean isSomeoneInBorrowing() {
    return getBookBorrowInfos().stream()
        .anyMatch(BookBorrowInfo::isInBorrowing);
  }

  public void borrow() {
    if (this.currentQuantity <= 0) {
      throw new BusinessException(this.title, "bookTitle", ErrorCode.BOOK_CANNOT_BORROW);
    }
    this.currentQuantity--;
  }
}
