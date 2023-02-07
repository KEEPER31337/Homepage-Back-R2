package com.keeper.homepage.domain.about.entity;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "static_write_subtitle_image")
public class StaticWriteSubtitleImage {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "subtitle", nullable = false)
  private String subtitle;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "static_write_title_id", nullable = false)
  private StaticWriteTitle staticWriteTitle;

  @ManyToOne(fetch = FetchType.LAZY, cascade = REMOVE)
  @JoinColumn(name = "thumbnail_id")
  private Thumbnail thumbnail;

  @Column(name = "display_order", nullable = false)
  private int displayOrder;

  @OneToMany(mappedBy = "staticWriteSubtitleImage", cascade = REMOVE)
  private final List<StaticWriteContent> staticWriteContents = new ArrayList<>();

  public void updateStaticWriteSubtitleImage(String subtitle, StaticWriteTitle staticWriteTitle,
      Thumbnail thumbnail, int displayOrder) {
    this.subtitle = subtitle;
    this.staticWriteTitle = staticWriteTitle;
    this.thumbnail = thumbnail;
    this.displayOrder = displayOrder;
  }

  @Builder
  private StaticWriteSubtitleImage(String subtitle, StaticWriteTitle staticWriteTitle,
      Thumbnail thumbnail, int displayOrder) {
    this.subtitle = subtitle;
    this.staticWriteTitle = staticWriteTitle;
    this.thumbnail = thumbnail;
    this.displayOrder = displayOrder;
  }
}
