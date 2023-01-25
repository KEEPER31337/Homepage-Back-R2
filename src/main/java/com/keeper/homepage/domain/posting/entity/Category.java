package com.keeper.homepage.domain.posting.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "category")
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "parent_id", nullable = false)
  private Long parent;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "href", nullable = false)
  private String href;

}
