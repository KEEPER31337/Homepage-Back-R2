package com.keeper.homepage.domain.about.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "static_write_content")
public class StaticWriteContent {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "content", columnDefinition = "TEXT")
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "static_write_subtitle_image_id", nullable = false)
  private StaticWriteSubtitleImage staticWriteSubtitleImage;

  @Column(name = "display_order", nullable = false)
  private int displayOrder;
}
