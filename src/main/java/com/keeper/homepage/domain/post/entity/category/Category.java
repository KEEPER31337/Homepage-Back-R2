package com.keeper.homepage.domain.post.entity.category;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.post.converter.CategoryTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Arrays;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@ToString(of = {"type"})
@EqualsAndHashCode(of = {"id"})
@Table(name = "category")
public class Category {

  private static final int MAX_NAME_LENGTH = 250;
  private static final int MAX_HREF_LENGTH = 45;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Convert(converter = CategoryTypeConverter.class)
  @Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
  private CategoryType type;

  public static Category getCategoryBy(CategoryType type) {
    return Category.builder()
        .id(type.id)
        .type(type)
        .build();
  }

  @Builder
  private Category(Long id, CategoryType type) {
    this.id = id;
    this.type = type;
  }

  @Getter
  @RequiredArgsConstructor
  public enum CategoryType {
    VIRTUAL_CATEGORY(1, "virtual_category"),
    공지사항(101, "공지사항"),
    건의사항(102, "건의사항"),
    정보게시판(103, "정보게시판"),
    자유게시판(104, "자유게시판"),
    익명게시판(105, "익명게시판"),
    졸업생게시판(106, "졸업생게시판"),
    시험게시판(107, "시험게시판"),
    스터디(201, "스터디"),
    발표자료(202, "발표자료"),
    기술문서(203, "기술문서"),
    회계부(204, "회계부"),
    ;

    private final long id;
    private final String name;

    public static CategoryType fromCode(String type) {
      return Arrays.stream(CategoryType.values())
          .filter(categoryType -> categoryType.getName().equals(type))
          .findAny()
          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 타입입니다."));
    }
  }
}
