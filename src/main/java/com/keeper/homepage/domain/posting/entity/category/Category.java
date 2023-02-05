package com.keeper.homepage.domain.posting.entity.category;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.posting.entity.Posting;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


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

  @Column(name = "parent_id")
  private Long parentId;

  @Column(name = "href", length = MAX_HREF_LENGTH)
  private String href;

  @OneToMany(mappedBy = "category", cascade = REMOVE, orphanRemoval = true)
  private final List<Posting> postings = new ArrayList<>();

  @Builder
  private Category(String name, Long parentId, String href) {
    this.name = name;
    this.parentId = parentId;
    this.href = href;
  }

  public void addPosting(Posting posting) {
    postings.add(posting);
  }
}
