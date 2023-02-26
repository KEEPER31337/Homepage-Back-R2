package com.keeper.homepage.domain.thumbnail.entity;

import com.keeper.homepage.domain.file.entity.FileEntity;
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
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Table(name = "thumbnail")
public class Thumbnail {

  public static final int MAX_PATH_LENGTH = 512;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "path", length = MAX_PATH_LENGTH)
  private String path;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "file_id", nullable = false)
  private FileEntity fileEntity;

  @Builder
  private Thumbnail(String path, FileEntity fileEntity) {
    this.path = path;
    this.fileEntity = fileEntity;
  }

  @Getter
  @RequiredArgsConstructor
  public enum DefaultThumbnail {
    MEMBER_THUMBNAIL(1, "keeper_files/thumbnail/default/default_thumbnail_member.png"),
    POST_THUMBNAIL(2, "keeper_files/thumbnail/default/default_thumbnail_posting.png"),
    ;

    private final long id;
    private final String path;
  }
}
