package com.keeper.homepage.domain.posting.entity.category;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.posting.entity.Posting;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import jakarta.persistence.*;
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
  private Category parentCategory;

  @Column(name = "href", length = MAX_HREF_LENGTH)
  private String href;

  @OneToMany(mappedBy = "category", cascade = ALL, orphanRemoval = true)
  private final List<Posting> postings = new ArrayList<>();

  @Builder
  private Category(String name, Category parentCategory, String href) {
    this.name = name;
    this.parentCategory = parentCategory;
    this.href = href;
  }

  public void addPosting(Posting posting) {
    posting.setCategory(this);
    postings.add(posting);
  }
}
