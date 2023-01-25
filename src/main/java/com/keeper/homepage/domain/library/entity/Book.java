package com.keeper.homepage.domain.library.entity;

import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "book")
public class Book {

  private static final int MAX_TITLE_LENGTH = 250;
  private static final int MAX_AUTHOR_LENGTH = 40;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "title", nullable = false, length = MAX_TITLE_LENGTH)
  private String title;

  @Column(name = "author", nullable = false, length = MAX_AUTHOR_LENGTH)
  private String author;

  @Column(name = "information", columnDefinition = "TEXT")
  private String information;

  @Column(name = "department", nullable = false)
  private Long department;

  @Column(name = "total", nullable = false)
  private Long total;

  @Column(name = "borrow", nullable = false)
  private Long borrow;

  @Column(name = "enable", nullable = false)
  private Long enable;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "thumbnail_id")
  private Thumbnail thumbnail;
}
