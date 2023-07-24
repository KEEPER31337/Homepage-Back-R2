package com.keeper.homepage.domain.post.entity.category;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@EqualsAndHashCode(of = {"id"})
@Table(name = "category")
public class Category {

  private static final int MAX_NAME_LENGTH = 250;
  private static final int MAX_HREF_LENGTH = 45;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
  private String name;

  @Builder
  private Category(String name) {
    this.name = name;
  }

  @Getter
  @RequiredArgsConstructor
  public enum CategoryType {
    VIRTUAL_CATEGORY(1),
    NOTICE_CATEGORY(101),
    SUGGESTION_CATEGORY(102),
    INFORMATION_CATEGORY(103),
    FREE_CATEGORY(104),
    ANONYMOUS_CATEGORY(105),
    GRADUATE_CATEGORY(106),
    EXAM_CATEGORY(107),
    ;

    private final long id;
  }
}
