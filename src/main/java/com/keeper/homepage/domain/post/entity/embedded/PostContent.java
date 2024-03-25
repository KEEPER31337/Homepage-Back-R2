package com.keeper.homepage.domain.post.entity.embedded;

import static jakarta.persistence.FetchType.LAZY;

import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostContent {

  private static final int MAX_TITLE_LENGTH = 250;
  private static final int MAX_IP_ADDRESS_LENGTH = 128;
  private static final int MAX_PASSWORD_LENGTH = 512;

  @OneToOne(fetch = LAZY)
  @JoinColumn(name = "thumbnail_id")
  private Thumbnail thumbnail;

  @Column(name = "title", nullable = false, length = MAX_TITLE_LENGTH)
  private String title;

  @Column(name = "content", nullable = false)
  private String content;

  public void changeThumbnail(Thumbnail thumbnail) {
    this.thumbnail = thumbnail;
  }

  public FileEntity getThumbnailFile() {
    return this.thumbnail.getFileEntity();
  }

  public void update(PostContent postContent) {
    this.title = postContent.getTitle();
    this.content = postContent.getContent();
  }

  public String getThumbnailPath() {
    return Optional.ofNullable(this.thumbnail)
        .map(Thumbnail::getPath)
        .orElse(null);
  }

  public void deleteThumbnail() {
    this.thumbnail = null;
  }

  @Builder
  private PostContent(Thumbnail thumbnail, String title, String content) {
    this.thumbnail = thumbnail;
    this.title = title;
    this.content = content;
  }
}
