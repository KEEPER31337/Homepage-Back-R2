package com.keeper.homepage.domain.library.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.library.converter.BookBorrowStatusTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Arrays;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "book_borrow_status")
public class BookBorrowStatus {

  private static final int MAX_TYPE_LENGTH = 20;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Convert(converter = BookBorrowStatusTypeConverter.class)
  @Column(name = "status", nullable = false, length = MAX_TYPE_LENGTH)
  private BookBorrowStatusType type;

  public static BookBorrowStatus getBookBorrowStatusBy(BookBorrowStatus.BookBorrowStatusType type) {
    return BookBorrowStatus.builder()
        .id(type.id)
        .type(type)
        .build();
  }

  @Builder
  private BookBorrowStatus(Long id, BookBorrowStatus.BookBorrowStatusType type) {
    this.id = id;
    this.type = type;
  }

  @Getter
  @RequiredArgsConstructor
  public enum BookBorrowStatusType {
    대출대기(1, "대출 대기"),
    대출반려(2, "대출 반려"),
    대출중(3, "대출 중"),
    반납대기(4, "반납 대기"),
    반납완료(5, "반납 완료"),
    ;

    private final long id;
    private final String status;

    public static BookBorrowStatus.BookBorrowStatusType fromCode(String type) {
      return Arrays.stream(BookBorrowStatus.BookBorrowStatusType.values())
          .filter(bookBorrowStatusType -> bookBorrowStatusType.getStatus().equals(type))
          .findAny()
          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도서 타입입니다."));
    }

    public static String getAllList() {
      return Arrays.stream(BookBorrowStatusType.values())
          .map(BookBorrowStatusType::name)
          .toList()
          .toString();
    }
  }

  public Long getId() {
    return id;
  }

  public BookBorrowStatusType getType() {
    return type;
  }
}
