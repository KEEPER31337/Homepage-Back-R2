package com.keeper.homepage.domain.library.entity;

import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Getter
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

  @Column(name = "information", columnDefinition = "TEXT")
  private String information;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department", nullable = false)
  private BookDepartment department;

  @Column(name = "total", nullable = false)
  private Long total;

  @Column(name = "borrow", nullable = false)
  private Long borrow;

  @Column(name = "enable", nullable = false)
  private Long enable;

  @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE)
  private final List<BookBorrowInfo> bookBorrowInfos = new ArrayList<>();

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "thumbnail_id")
  private Thumbnail thumbnail;

  @Builder
  private Book(String title, String author, String information, BookDepartment department,
      Long total, Long borrow, Long enable, Thumbnail thumbnail) {
    this.title = title;
    this.author = author;
    this.information = information;
    this.department = department;
    this.total = total;
    this.borrow = borrow;
    this.enable = enable;
    this.thumbnail = thumbnail;
  }

  public void addBookBorrowInfo(BookBorrowInfo bookBorrowInfo) {
    this.getBookBorrowInfos().add(bookBorrowInfo);
  }
}
