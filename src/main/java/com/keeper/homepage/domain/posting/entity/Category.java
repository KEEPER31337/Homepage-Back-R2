package com.keeper.homepage.domain.posting.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.*;
import lombok.AccessLevel;
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
}
