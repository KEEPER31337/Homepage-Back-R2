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
    공지사항(101),
    건의사항(102),
    정보게시판(103),
    자유게시판(104),
    익명게시판(105),
    졸업생게시판(106),
    시험게시판(107),
    발표자료(202),
    기술문서(203),
    회계부(204),
    ;

    private final long id;
  }
}
