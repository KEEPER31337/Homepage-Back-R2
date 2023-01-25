package com.keeper.homepage.domain.library.entity;

import static java.lang.String.format;

import com.keeper.homepage.domain.about.converter.StaticWriteTitleTypeConverter;
import com.keeper.homepage.domain.about.entity.StaticWriteTitle;
import com.keeper.homepage.domain.about.entity.StaticWriteTitle.StaticWriteTitleType;
import com.keeper.homepage.domain.library.converter.BookDepartmentTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.Builder;
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

  @Convert(converter = BookDepartmentTypeConverter.class)
  @Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
  private BookDepartmentType name;

  public static BookDepartment getBookDepartmentBy(BookDepartmentType name) {
    return BookDepartment.builder()
        .id(name.id)
        .name(name)
        .build();
  }

  @Builder
  private BookDepartment(Long id, BookDepartmentType name) {
    this.id = id;
    this.name = name;
  }

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
