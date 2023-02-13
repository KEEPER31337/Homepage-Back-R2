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

  @OneToMany(mappedBy = "category", cascade = ALL, orphanRemoval = true)
  private final List<Post> posts = new ArrayList<>();

  @OneToMany(mappedBy = "parent", cascade = ALL, orphanRemoval = true)
  private final List<Category> children = new ArrayList<>();

  @Builder
  private Category(String name, Category parent, String href) {
    this.name = name;
    this.parent = parent;
    this.href = href;
  }

  public void addPost(Post post) {
    post.assignCategory(this);
    posts.add(post);
  }

  public void addChild(Category category) {
    category.assignParent(this);
    children.add(category);
  }

  public void assignParent(Category parent) {
    this.parent = parent;
  }
}
