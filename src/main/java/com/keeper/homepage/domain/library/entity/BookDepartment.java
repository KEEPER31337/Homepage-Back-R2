package com.keeper.homepage.domain.library.entity;

import static java.lang.String.format;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "book_department")
public class BookDepartment {

  private static final int MAX_NAME_LENGTH = 45;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
  private BookDepartmentType name;

  @Getter
  @RequiredArgsConstructor
  public enum BookDepartmentType {
    LANGUAGE(1, "language"),
    SECURITY(2, "security"),
    TEXTBOOK(3, "textbook"),
    CERTIFICATION(4, "certification"),
    DOCUMENT(5, "document"),
    ETC(6, "etc");

    private final long id;
    private final String name;

    public static BookDepartmentType fromCode(String dbData) {
      return Arrays.stream(BookDepartmentType.values())
          .filter(type -> type.getName().equals(dbData))
          .findAny()
          .orElseThrow(() -> new IllegalArgumentException(format("%s 타입이 DB에 존재하지 않습니다.", dbData)));
    }
  }
}
