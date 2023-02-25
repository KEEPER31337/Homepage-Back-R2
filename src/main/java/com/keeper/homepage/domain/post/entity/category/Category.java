package com.keeper.homepage.domain.post.entity.category;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.post.entity.Post;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
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

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "parent_id")
  private Category parent;

  @Column(name = "href", length = MAX_HREF_LENGTH)
  private String href;

  @OneToMany(mappedBy = "category")
  private final List<Post> posts = new ArrayList<>();

  @OneToMany(mappedBy = "parent", cascade = ALL, orphanRemoval = true)
  private final List<Category> children = new ArrayList<>();

  @Builder
  private Category(String name, Category parent, String href) {
    this.name = name;
    this.parent = parent;
    this.href = href;
  }

  @Getter
  @RequiredArgsConstructor
  public enum DefaultCategory {
    VIRTUAL_CATEGORY(1, "virtual_category"),
    EXAM_CATEGORY(1377, "시험"),
    ANONYMOUS_CATEGORY(63908, "익명게시판"),
    ;

    private final long id;
    private final String name;
  }

  public void addChild(Category child) {
    child.assignParent(this);
    children.add(child);
  }

  public void assignParent(Category parent) {
    this.parent = parent;
  }
}
