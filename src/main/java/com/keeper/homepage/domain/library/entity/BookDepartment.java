package com.keeper.homepage.domain.library.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.library.converter.BookDepartmentTypeConverter;
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
@Table(name = "book_department")
public class BookDepartment {

  private static final int MAX_TYPE_LENGTH = 45;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Convert(converter = BookDepartmentTypeConverter.class)
  @Column(name = "name", nullable = false, length = MAX_TYPE_LENGTH)
  private BookDepartmentType type;

  public static BookDepartment getBookDepartmentBy(BookDepartmentType type) {
    return BookDepartment.builder()
        .id(type.id)
        .type(type)
        .build();
  }

  @Builder
  private BookDepartment(Long id, BookDepartmentType type) {
    this.id = id;
    this.type = type;
  }

  @Getter
  @RequiredArgsConstructor
  public enum BookDepartmentType {
    LANGUAGE(1, "language"),
    SECURITY(2, "security"),
    TEXTBOOK(3, "textbook"),
    CERTIFICATION(4, "certification"),
    DOCUMENT(5, "document"),
    ETC(6, "etc"),
    ;

    private final long id;
    private final String name;

    public static BookDepartmentType fromCode(String type) {
      return Arrays.stream(BookDepartmentType.values())
          .filter(bookDepartmentType -> bookDepartmentType.getName().equals(type))
          .findAny()
          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도서 타입입니다."));
    }
  }

  public Long getId() {
    return id;
  }

  public BookDepartmentType getType() {
    return type;
  }
}
